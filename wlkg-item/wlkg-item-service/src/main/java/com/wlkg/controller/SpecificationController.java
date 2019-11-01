package com.wlkg.controller;

import com.wlkg.pojo.SpecGroup;
import com.wlkg.pojo.SpecParam;
import com.wlkg.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid){
        List<SpecGroup> list=specificationService.querySpecGroups(cid);
        if(list.size()==0||list==null){
            return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /*@GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParam(@RequestParam(value="gid", required = false) Long gid){
        List<SpecParam> list = this.specificationService.querySpecParams(gid);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }*/
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParam(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic
    ){
        List<SpecParam> list =
                this.specificationService.querySpecParams(gid,cid,searching,generic);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 编辑规格组
     * @param specGroup
     */
    @PutMapping("/group")
    public void editSpecGroup(@RequestBody SpecGroup specGroup){
        specificationService.editSpecGroup(specGroup);
    }

    /**
     * 添加规格组
     * @param specGroup
     */
    @PostMapping("/group")
    public void AddSpecGroup(@RequestBody SpecGroup specGroup){
        specificationService.addSpecGroup(specGroup);
    }

    /**
     * 删除规格组
     * @param id
     */
    @DeleteMapping("/group/{id}")
    public void deleteSpecGroup(@PathVariable(value = "id")Long id){
        specificationService.deleteSpecGroup(id);
    }

    /**
     * 编辑规格参数
     * @param specParam
     */
    @PutMapping("/param")
    public void editSpecGroup(@RequestBody SpecParam specParam){
        specificationService.editSpecParam(specParam);
    }

    /**
     * 添加规格参数
     * @param specParam
     */
    @PostMapping("/param")
    public void addSpecParam(@RequestBody SpecParam specParam){
        specificationService.addSpecParam(specParam);
    }

    /**
     * 删除规格参数
     * @param id
     */
    @DeleteMapping("/param/{id}")
    public void deleteSpecParam(@PathVariable(value = "id") Long id){
        specificationService.deleteSpecParam(id);
    }
}
