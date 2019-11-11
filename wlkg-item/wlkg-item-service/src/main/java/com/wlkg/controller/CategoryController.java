package com.wlkg.controller;

import com.wlkg.pojo.Category;
import com.wlkg.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam("pid")Long pid){
        List<Category> list = categoryService.queryCategoryByPid(pid);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam("id") Long id){
         categoryService.deleteCategoryById(id);
    }

    @PutMapping("/edit")
    public void edit(@RequestParam("id") Long id,@RequestParam("name")String name){
        Category category=categoryService.selectOneById(id);
        category.setName(name);
        categoryService.edit(category);
    }

    @PutMapping("/add")
    public void add(@RequestBody Category category){
         categoryService.add(category);
    }

    @GetMapping("/bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid")Long bid){
        List<Category> list=categoryService.queryByBrandId(bid);
        if(list==null||list.size()==0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 根据Id查询商品分类
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids")List<Long> ids){
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }

}
