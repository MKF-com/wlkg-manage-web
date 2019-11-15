package com.wlkg;

import com.wlkg.auth.client.CategoryClient;
import com.wlkg.auth.client.GoodsClient;
import com.wlkg.common.pojo.PageResult;
import com.wlkg.pojo.Category;
import com.wlkg.pojo.Goods;
import com.wlkg.pojo.Spu;
import com.wlkg.repository.GoodsRepository;
import com.wlkg.auth.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BuildIndex {
    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    public void test01(){
        List<Category> list = categoryClient.queryCategoryByIds(Arrays.asList(1L, 2L, 3L));
        list.forEach(e->System.out.println(e.getName()));
    }
    @Test
    public void createIndex(){
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }


    @Test
    public void loadData(){

        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            // 查询分页数据
            PageResult<Spu> result = goodsClient.querySpuByPage(page, rows, true, null);
            List<Spu> spus = result.getItems();
            size = spus.size();
            // 创建Goods集合
            List<Goods> goodsList = new ArrayList<>();
            // 遍历spu
            for (Spu spu : spus) {
                try {
                    Goods goods = searchService.buildGoods(spu);
                    goodsList.add(goods);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (goodsList != null && goodsList.size() > 0){
                goodsRepository.saveAll(goodsList);
            }
            page++;
        } while (size == 100);
    }

}
