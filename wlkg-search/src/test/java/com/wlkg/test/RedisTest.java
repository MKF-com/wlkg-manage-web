package com.wlkg.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wlkg.pojo.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedis() {
        // 存储数据
        this.redisTemplate.opsForValue().set("name", "zhangsan");
        // 获取数据
        String val = this.redisTemplate.opsForValue().get("name");
        System.out.println("val = " + val);
    }

    @Test
    public void testRedis2() {
        // 存储数据，并指定剩余生命时间,5小时
        this.redisTemplate.opsForValue().set("age", "15",
                5, TimeUnit.HOURS);
    }

    @Test
    public void testHash(){
        BoundHashOperations<String, Object, Object> hashOps =
                this.redisTemplate.boundHashOps("user1");
        // 操作hash数据
        hashOps.put("name", "jack");
        hashOps.put("age", "21");

        // 获取单个数据
        Object name = hashOps.get("name");
        System.out.println("name = " + name);

        // 获取所有数据
        Map<Object, Object> map = hashOps.entries();
        for (Map.Entry<Object, Object> me : map.entrySet()) {
            System.out.println(me.getKey() + " : " + me.getValue());
        }
    }

    @Test
    public void test01(){
        BoundHashOperations<String,Object,Object> user=redisTemplate.boundHashOps("user2");
        user.put("name","wangwu");
        user.put("age","99");
        user.put("location","shanghai");
    }

    @Test
    public void test02(){
        BoundHashOperations<String,Object,Object> user=redisTemplate.boundHashOps("user2");
        Map<Object,Object> res=user.entries();
        for(Map.Entry<Object,Object> set:res.entrySet()){
            System.out.println(set.getKey()+":"+set.getValue());
        }
    }

    @Test
    public void test03(){
        Student s=new Student(1001,"张三","shanghai");
        ObjectMapper obj = new ObjectMapper();
        try {
            String json=obj.writeValueAsString(s);
            redisTemplate.opsForValue().set("student",json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test04() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json=redisTemplate.opsForValue().get("student");
        Student s=objectMapper.readValue(json,Student.class);
        System.out.println(s);
    }
}
