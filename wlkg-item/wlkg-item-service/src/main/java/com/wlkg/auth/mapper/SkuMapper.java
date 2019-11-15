package com.wlkg.auth.mapper;

import com.wlkg.common.mapper.BaseMapper;
import com.wlkg.pojo.Sku;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SkuMapper extends BaseMapper<Sku,Long> {
    @Delete("delete from tb_sku where spu_id=#{id}")
    void deleteSkuBySpuId(Long id);

    @Select("select id from tb_sku where spu_id=#{id}")
    List<Long> querySkuIds(Long id);
}
