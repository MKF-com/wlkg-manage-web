package com.wlkg.auth.service;

import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.auth.mapper.SpecGroupMapper;
import com.wlkg.auth.mapper.SpecParamMapper;
import com.wlkg.pojo.SpecGroup;
import com.wlkg.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    SpecParamMapper specParamMapper;

    public List<SpecGroup> querySpecGroups(Long cid) {
        SpecGroup t = new SpecGroup();
        t.setCid(cid);
        return specGroupMapper.select(t);
    }

    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam t = new SpecParam();
        t.setGroupId(gid);
        t.setCid(cid);
        t.setSearching(searching);
        t.setGeneric(generic);

        List<SpecParam> list = this.specParamMapper.select(t);
        if (CollectionUtils.isEmpty(list)) {
            throw new WlkgException(ExceptionEnums.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

    public void editSpecGroup(SpecGroup specGroup) {
        specGroupMapper.updateByPrimaryKeySelective(specGroup);
    }

    public void addSpecGroup(SpecGroup specGroup) {
        specGroupMapper.insertSelective(specGroup);
    }

    public void editSpecParam(SpecParam specParam) {
        specParamMapper.updateByPrimaryKeySelective(specParam);
    }

    public void addSpecParam(SpecParam specParam) {
        specParamMapper.insertSelective(specParam);
    }

    public void deleteSpecParam(Long id) {
        specParamMapper.deleteByPrimaryKey(id);
    }

    public void deleteSpecGroup(Long id) {
        //删除该组下的参数
        specParamMapper.deleteByGroupId(id);
        //删除该组
        specGroupMapper.deleteByPrimaryKey(id);
    }

    public List<SpecGroup> querySpecsByCid(Long cid) {
        //查询规格组
        List<SpecGroup> groups=querySpecGroups(cid);
        //查询当前组下的参数
        List<SpecParam> params=querySpecParams(null,cid,null,null);
        Map<Long,List<SpecParam>> map=new HashMap<>();
        for(SpecParam param:params){
            if(!map.containsKey(param.getGroupId())){
                //如果这个组id在map中不存在，新增一个list
                map.put(param.getGroupId(),new ArrayList<>());
            }
            map.get(param.getGroupId()).add(param);
        }

        //填充param到group中
        for(SpecGroup group:groups){
            group.setParams(map.get(group.getId()));
        }
        return groups;
    }
}