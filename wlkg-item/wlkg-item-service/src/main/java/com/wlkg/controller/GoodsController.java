package com.wlkg.controller;

import com.wlkg.common.pojo.PageResult;
import com.wlkg.pojo.Sku;
import com.wlkg.pojo.Spu;
import com.wlkg.pojo.SpuDetail;
import com.wlkg.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询spu信息
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable){
        PageResult<Spu> result=goodsService.querySpuByPage(page,rows,key,saleable);
        return ResponseEntity.ok(result);
    }

    /**
     * 商品添加
     * @param spu
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        try {
            goodsService.saveGoods(spu);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 编辑商品信息
     * @param spu
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> editGoods(@RequestBody Spu spu){
        goodsService.editGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /**
     * 查询商品详情信息
     * 数据回显
     * @param id
     * @return
     */
    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable(value = "id")Long id ){
        SpuDetail detail=goodsService.queryDetailById(id);
        if(detail==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(detail);
    }

    /**
     * 查询sku
     * 数据回显
     * @param id
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id) {
        List<Sku> skus =goodsService.querySkuBySpuId(id);
        if (skus == null || skus.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(skus);
    }

    /**
     * 删除商品
     * @param id
     */
    @DeleteMapping("/spu/delete/{id}")
    public void deleteSpuById(@PathVariable(value = "id")Long id){
        goodsService.deleteSpuById(id);
    }

    /**
     * 商品下架
     * @param id
     */
    @PutMapping("/spu/down/{id}")
    public void downSpu(@PathVariable(value = "id")Long id){
        goodsService.downSpu(id);
    }

    /**
     * 上架商品
     * @param id
     */
    @PutMapping("/spu/up/{id}")
    public void upSpu(@PathVariable(value = "id")Long id){
        goodsService.upSpu(id);

    }

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu = this.goodsService.querySpuById(id);
        return ResponseEntity.ok(spu);
    }

}
