package com.wlkg.auth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wlkg.auth.client.BrandClient;
import com.wlkg.auth.client.CategoryClient;
import com.wlkg.auth.client.GoodsClient;
import com.wlkg.auth.client.SpecificationClient;
import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.common.pojo.PageResult;
import com.wlkg.common.utils.JsonUtils;
import com.wlkg.pojo.*;
import com.wlkg.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specClient;

    public Goods buildGoods(Spu spu){

        //查询分类
        List<Category> categories= categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        if(CollectionUtils.isEmpty(categories)){
            throw new WlkgException(ExceptionEnums.CATEGORY_NOT_FOUND);
        }
        List<String> names=categories.stream().map(Category::getName).collect(Collectors.toList());

        //查询品牌
        Brand brand=brandClient.queryBrandById(spu.getBrandId());
        if(brand==null){
            throw new WlkgException(ExceptionEnums.BRAND_NOT_FOUND);
        }

        //搜索字段
        String all=spu.getTitle()+ StringUtils.join(names,"")+brand.getName();

        //查询sku
        List<Sku> skus=goodsClient.querySkuBySpuId(spu.getId());
        if(CollectionUtils.isEmpty(skus)){
            throw new WlkgException(ExceptionEnums.GOODS_SKU_NOT_FOUND);
        }

        // 处理sku，仅封装id、价格、标题、图片，并获得价格集合
        List<Long> prices=new ArrayList<>();
        List<Map<String,Object>> skuList=new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String,Object> skuMap =new HashMap<>();
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("price",sku.getPrice());
            skuMap.put("image",StringUtils.substringBefore(sku.getImages(),","));
            skuList.add(skuMap);
        });

        //查询规格参数
        List<SpecParam> params=specClient.querySpecParam(null,spu.getCid3(),true,null);
        //查询商品详情
        SpuDetail spuDetail=goodsClient.querySpuDetailById(spu.getId());

        //获取通用规格参数
        Map<Long,String> genericSpec=JsonUtils.parseMap(spuDetail.getGenericSpec(),Long.class,String.class);
        //获取特有规格参数
        Map<Long,List<String>> specialSpec=JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {});

        //定义spec对应的map
        HashMap<String,Object> specs=new HashMap<>();
        //对规格进行遍历，并封装spec，其中spec的key是规格参数的名称，值是商品详情中的值
        for(SpecParam param:params){
            //key是规格参数的名称
            String key=param.getName();
            Object value="";

            if(param.getGeneric()){
                //参数是通用属性，通过规格参数的ID从商品详情存储的规格参数中查出值
                value=genericSpec.get(param.getId());
                if(param.getNumeric()){
                    //参数是数值类型，处理成段，方便后期对数值类型进行范围过滤
                    value=chooseSegment(value.toString(),param);
                }
            }
            else{
                //参数不是通用类型
                value=specialSpec.get(param.getId());
            }
            value=(value==null?"其他":value);
            //存入map
            specs.put(key,value);
        }

        Goods goods = new Goods();

        goods.setId(spu.getId());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());

        goods.setAll(all);//
        goods.setPrice(prices);// 设置价格
        goods.setSkus(JsonUtils.serialize(skuList));//设置sku
        goods.setSpecs(specs);//设置规格
        return goods;
    }

    private Object chooseSegment(String Value, SpecParam p) {
        double val= NumberUtils.toDouble(Value);
        String result="其他";
        //保存数值段
        for(String segment:p.getSegments().split(",")){
            String[] segs=segment.split("-");
            //获取数值范围
            double begin=NumberUtils.toDouble(segs[0]);
            double end=Double.MAX_VALUE;
            if(segs.length==2){
                end=NumberUtils.toDouble(segs[1]);
            }
            //判断是否在范围内
            if(val>=begin && val<end){
                if(segs.length==1){
                    result=segs[0]+p.getUnit()+"以上    ";
                }else if(begin == 0) {
                    result = segs[1] + p.getUnit() + "以下    ";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request) {
        String key=request.getKey();
        //判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if(StringUtils.isBlank(key)){
            return null;
        }

        //构建搜索条件
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();

        //对key进行全文搜索
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",key).operator(Operator.AND));

        //通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));

        //分页
        //准备分页参数
        int page=request.getPage();
        int size= request.getSize();queryBuilder.withPageable(PageRequest.of(page-1,size));

        //查询
        Page<Goods> pageInfo= goodsRepository.search(queryBuilder.build());

        //封装结果并返回
        List<Goods> goods=pageInfo.getContent();
        Long total=pageInfo.getTotalElements();
        int totalPage=pageInfo.getTotalPages();
        return new PageResult<Goods>(total, (long) totalPage,goods);
    }

    public void createIndex(Long id) throws IOException {
        //查询spu
        Spu spu = goodsClient.querySpuById(id);
        //构建goods
        Goods goods = buildGoods(spu);
        //存入索引库
        goodsRepository.save(goods);
    }

    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }

}
