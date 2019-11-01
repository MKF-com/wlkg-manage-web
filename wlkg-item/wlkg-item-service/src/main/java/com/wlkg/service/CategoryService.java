package com.wlkg.service;

import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.mapper.CategoryMapper;
import com.wlkg.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryByBrandId(Long bid) {
        return categoryMapper.queryByBrandId(bid);
    }

    public List<Category> queryCategoryByPid(Long pid) {
        Category c = new Category();
        c.setParentId(pid);
        List<Category> list= categoryMapper.select(c);
        if(CollectionUtils.isEmpty(list)){
            //空数据
            throw new WlkgException(ExceptionEnums.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    public Category selectOneById(Long id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    public int edit(Category category) {
        return categoryMapper.updateByPrimaryKeySelective(category);
    }

    public void add(Category category) {
        categoryMapper.insertSelective(category);
        Category category1=categoryMapper.selectByPrimaryKey(category.getParentId());
        category1.setIsParent(true);
        categoryMapper.updateByPrimaryKeySelective(category1);
    }

    public void deleteCategoryById(Long id){
        List<Category> list=selectBrotherNodes(id);
        if(list.size()==0){
            Category category=categoryMapper.selectByPrimaryKey(id);
            Long parentId = category.getParentId();
            categoryMapper.deleteByPrimaryKey(id);
            this.check(parentId);
        }else{
            for(Category category:list){
                if(category.getIsParent()){
                    deleteCategoryById(category.getId());
                }else{
                    categoryMapper.deleteByPrimaryKey(category.getId());
                }
            }
            categoryMapper.deleteByPrimaryKey(id);
        }
    }

    public void check(Long parentId){
        List<Category> list = selectBrotherNodes(parentId);
        if(list.size()==0){
            Category category=categoryMapper.selectByPrimaryKey(parentId);
            category.setIsParent(false);
            categoryMapper.updateByPrimaryKeySelective(category);
        }
    }

    //查询子结点
    public List<Category> selectBrotherNodes(Long id){
        Example example=new Example(Category.class);
        example.createCriteria().andEqualTo("parentId",id);
        return categoryMapper.selectByExample(example);
    }

    /**
     * 查询商品分类的名字
     * @return
     */
    public List<String> queryNamesByIds(List<Long> ids) {
        List<String> list=new ArrayList<>();
        for(Long cid:ids){
            String name=categoryMapper.queryNameBiId(cid);
            list.add(name);
        }
        return list;
/*
        return this.categoryMapper.selectByIdList(ids).stream().map(Category::getName).collect(Collectors.toList());
*/
    }

    /**
     * 查询商品分类的名字
     * @param ids
     * @return
     */
    public List<String> queryNameByIds(List<Long> ids) {
        return this.categoryMapper.selectByIdList(ids).stream().map(Category::getName).collect(Collectors.toList());
    }

}
