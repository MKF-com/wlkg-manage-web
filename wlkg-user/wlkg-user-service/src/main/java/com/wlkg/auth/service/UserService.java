package com.wlkg.auth.service;

import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.common.utils.CodecUtils;
import com.wlkg.common.utils.NumberUtils;
import com.wlkg.auth.mapper.UserMapper;
import com.wlkg.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String KEY_PREFIX = "user:code:phone:";

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Boolean checkData(String data, Integer type) {
        User user=new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        List<User> list=userMapper.select(user);
        if(list.size()>0){
            if(list.get(0)!=null){
                return false;
            }
        }
        return true;
    }

    public Boolean sendVerifyCode(String phone) {
        //生成验证码
        String code= NumberUtils.generateCode(6);
        try {
            //发送短信
            Map<String,String> map=new HashMap<>();
            map.put("phone",phone);
            map.put("code",code);
//            amqpTemplate.convertAndSend("wlkg.sms.exchange", "sms.verify.code", map);
            //将code存入redis
            redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return true;
        }catch (Exception e){
            logger.error("发送短信失败。phone：{}， code：{}", phone, code);
            return false;

        }
    }

    public void register(User user, String code) {
        String key=KEY_PREFIX+user.getPhone();
        //从redis中取出验证码
        String codeCache=redisTemplate.opsForValue().get(key);
        //检查验证码是否正确
        if(!code.equals(codeCache)){
            //不正确，返回
            throw new WlkgException(ExceptionEnums.INVALID_VERFIY_CODE);
        }
        user.setCreated(new Date());
        //生成盐
        String salt= CodecUtils.generateSalt();
        user.setSalt(salt);
        //对密码进行加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //写入数据库
        boolean boo=userMapper.insertSelective(user)==1;

        //如果注册成功，删除redis中的code
        if(boo){
            try {
                redisTemplate.delete(key);
            }catch (Exception e){
                logger.error("删除缓存验证码失败,code:",code,e);
            }
        }
    }

    public User queryUser(String username, String password) {
        // 查询
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        // 校验用户名
        if (user == null) {
            throw new WlkgException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
        }
        // 校验密码
        if (!user.getPassword().equals(CodecUtils.md5Hex(password, user.getSalt()))) {
            throw new WlkgException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
        }
        // 用户名密码都正确
        return user;
    }

}
