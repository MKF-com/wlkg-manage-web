package com.wlkg.auth.controller;

import com.wlkg.auth.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class GoodsController {

    @Autowired
    private PageService pageService;

    /**
     * 跳转到商品详情页面
     */
   @GetMapping("item/{id}.html")
    public String toItemPage(ModelMap modelMap, @PathVariable("id")Long id){
        Map<String,Object> map=pageService.loadModel(id);
        modelMap.addAllAttributes(map);
       // 判断是否需要生成新的页面
       if(!this.pageService.exists(id)){
           this.pageService.syncCreateHtml(id);
       }

       return "item";
    }

    //@GetMapping("item/{id}.html")
    @ResponseBody
    public Map<String,Object> toItemPage1(@PathVariable("id")Long id){
        Map<String,Object> map=pageService.loadModel(id);
        //model.addAllAttributes(map);
        return map;

    }

}
