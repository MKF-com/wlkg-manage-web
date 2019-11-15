package com.wlkg.repository;

import com.wlkg.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 飞鸟
 * @create 2019-11-06 15:19
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
