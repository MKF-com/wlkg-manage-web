package com.wlkg.auth.controller;

import com.wlkg.common.pojo.PageResult;
import com.wlkg.pojo.Goods;
import com.wlkg.pojo.SearchRequest;
import com.wlkg.auth.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    //根据关键词搜索
    //请求方式：post
    //url:/page
    //请求参数：key ,page,size
    //返回类型：PageResult<Goods>

    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request){
        return ResponseEntity.ok(searchService.search(request));
    }

}
