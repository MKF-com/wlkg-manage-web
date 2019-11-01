package com.wlkg.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.common.pojo.PageResult;
import com.wlkg.mapper.*;
import com.wlkg.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.print.attribute.standard.PagesPerMinute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class GoodsService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    /**
     * 分页查询spu
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, String key, Boolean saleable) {
        //分页，最多允许查100条数据
        PageHelper.startPage(page,Math.min(rows,100));
        //创建查询条件
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //过滤上下架
        if(saleable!=null){
            criteria.orEqualTo("saleable",saleable);
        }

        //模糊查询
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }

        //默认排序
        example.setOrderByClause("last_update_time desc");

        List<Spu> list=spuMapper.selectByExample(example);

        PageInfo<Spu> pageInfo=new PageInfo<>(list);
        if(CollectionUtils.isEmpty(list)){
            throw new  WlkgException(ExceptionEnums.GOODS_NOT_FOUND);
        }

        for(Spu spu:list){
            //查询商品分类名称
            List<String> names=categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
            // 将分类名称拼接后存入
            spu.setCname(StringUtils.join(names, "/"));
            // 查询spu的品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spu.setBname(brand.getName());
        }
        PageResult<Spu> result=new PageResult<>();
        result.setTotalPage(Long.valueOf(pageInfo.getPages()));
        result.setTotal(pageInfo.getTotal());
        result.setItems(list);
        return result;
    }

    /**
     * 新增商品
     * @param spu
     */
    @Transactional
    public void saveGoods(Spu spu) {
        //添加商品
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spuMapper.insert(spu);

        //添加商品详情
        SpuDetail spuDetail=spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);

        saveSkuAndStock(spu);
    }

    /**
     * 添加sku和stock
     * @param spu
     */
    public void saveSkuAndStock(Spu spu){
        List<Sku> skus=spu.getSkus();
        List<Stock> stocks=new ArrayList<>();
        for(Sku sku:skus) {
            sku.setSpuId(spu.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insert(sku);

            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock().intValue());
            stocks.add(stock);
        }
        stockMapper.insertList(stocks);
    }

    /**
     * 查询商品详情
     * 数据回显
     * @param id
     * @return
     */
    public SpuDetail queryDetailById(Long id) {
        return spuDetailMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询sku和库存stock
     * 数据回显
     * @param id
     * @return
     */
    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku=new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus=skuMapper.select(sku);

        //查询库存
        for(Sku sku1:skus){
            Stock stock=stockMapper.selectByPrimaryKey(sku1.getId());
            sku1.setStock(stock.getStock());
        }
        return skus;
    }

    /**
     * 删除商品
     * @param id
     */
    @Transactional
    public void deleteSpuById(Long id) {
        //删除spu
        spuMapper.deleteByPrimaryKey(id);
        //删除spuDetail
        spuDetailMapper.deleteByPrimaryKey(id);
        //删除stock
        Sku sku=new Sku();
        sku.setSpuId(id);
        List<Sku> skus=skuMapper.select(sku);
        //删除sku
        skuMapper.deleteSkuBySpuId(id);
        //删除stock
        for(Sku sku1:skus){
            stockMapper.deleteByPrimaryKey(sku1.getId());
        }
    }

    /**
     * 编辑商品
     * @param spu
     */
    public void editGoods(Spu spu) {
        //修改spu
        spu.setSaleable(null);
        spu.setValid(null);
        spu.setCreateTime(null);
        spu.setLastUpdateTime(new Date());
        int n=spuMapper.updateByPrimaryKeySelective(spu);
        if(n<1){
            throw new WlkgException((ExceptionEnums.GOODS_UPDATE_FAILED));
        }
        //修改spudetail
        SpuDetail spuDetail=spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.updateByPrimaryKeySelective(spuDetail);

        //修改sku和stock
        Sku sku=new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skus=skuMapper.select(sku);
        //删除sku
        skuMapper.deleteSkuBySpuId(spu.getId());
        //删除stock
        for(Sku sku1:skus){
            stockMapper.deleteByPrimaryKey(sku1.getId());
        }
        //添加sku和stock
        saveSkuAndStock(spu);
    }

    /**
     * 商品下架
     * @param id
     */
    public void downSpu(Long id) {
        Spu spu=spuMapper.selectByPrimaryKey(id);
        spu.setSaleable(false);
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 上架商品
     * @param id
     */
    public void upSpu(Long id) {
        Spu spu=spuMapper.selectByPrimaryKey(id);
        spu.setSaleable(true);
        spuMapper.updateByPrimaryKeySelective(spu);
    }
}
