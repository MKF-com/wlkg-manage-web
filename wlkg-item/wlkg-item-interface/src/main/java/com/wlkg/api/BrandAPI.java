package com.wlkg.api;

import com.wlkg.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface BrandAPI {
    @GetMapping("brand/{id}")
    Brand queryBrandById(@PathVariable("id") Long id) ;
}
