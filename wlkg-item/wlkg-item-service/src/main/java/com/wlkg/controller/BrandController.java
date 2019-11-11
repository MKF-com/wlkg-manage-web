package com.wlkg.controller;

import com.wlkg.common.pojo.PageResult;
import com.wlkg.pojo.Brand;
import com.wlkg.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/brand/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key){
        PageResult<Brand> result=brandService.selectBrandByPage(page, rows, sortBy, desc,key);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/brand")
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids") List<Long> cids){
        brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/brand")
    public void editBrand(Brand brand,@RequestParam("cids") List<Long> cids){
        brandService.editBrand(brand,cids);
    }

    @DeleteMapping("/brand")
    public void deleteBrand(@RequestParam("id") Long id){
        brandService.deleteBrand(id);
    }

    /**
     * 根据分类查询品牌
     *
     * @param cid
     * @return
     */
    @GetMapping("brand/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCategory(@PathVariable("cid") Long cid) {
        List<Brand> list = this.brandService.queryBrandByCategory(cid);
        if (CollectionUtils.isEmpty(list)) {
            // 响应404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(list);
    }


    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("brand/{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(brandService.queryById(id));
    }


}
