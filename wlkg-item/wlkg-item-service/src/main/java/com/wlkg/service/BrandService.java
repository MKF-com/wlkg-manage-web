package com.wlkg.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wlkg.common.pojo.PageResult;
import com.wlkg.mapper.BrandMapper;
import com.wlkg.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> selectBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //开始分页
        PageHelper.startPage(page,rows);
        Example example=new Example(Brand.class);
        if(StringUtils.isNotBlank(key)){
            example.createCriteria().andLike("name","%"+key+"%").orEqualTo("letter",key);
        }

        if(StringUtils.isNotBlank(sortBy)){
            String str= sortBy +( desc ?" DESC":" ASC");
            example.setOrderByClause(str);
        }
        List<Brand> list=brandMapper.selectByExample(example);
        PageInfo<Brand> pageInfo=new PageInfo<>(list);
        PageResult<Brand> result=new PageResult<>();
        result.setItems(list);
        result.setTotal(pageInfo.getTotal());
        result.setTotalPage(Long.valueOf(pageInfo.getPages()));
        return result;

    }

    @Transactional
    public void saveBrand(Brand brand,List<Long> cids) {
        //新增品牌信息
        brandMapper.insertSelective(brand);
        for(Long cid:cids){
            brandMapper.insertCategoryBrand(cid,brand.getId());
        }
    }

    @Transactional
    public void editBrand(Brand brand, List<Long> cids) {
        //更新商品表
        brandMapper.updateByPrimaryKeySelective(brand);
        long bid=brand.getId();
        //删除该商品对应的分类
        brandMapper.deleteCategoryBrand(bid);
        //添加新的分类
        for(Long cid:cids){
            brandMapper.insertCategoryBrand(cid,bid);
        }
    }

    @Transactional
    public void deleteBrand(Long id) {
        //删除该商品
        brandMapper.deleteByPrimaryKey(id);
        //删除中间表中该商品的分类
        brandMapper.deleteCategoryBrand(id);
    }

    public List<Brand> queryBrandByCategory(Long cid) {
        List<Brand> list=brandMapper.queryByCategoryId(cid);
        return list;
    }
}