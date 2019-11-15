package com.wlkg.api;

import com.wlkg.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {
    @GetMapping("/query")
    User queryUsernameAndPassword(@RequestParam("username") String username, @RequestParam("password") String password);
    @PostMapping("code")
    Void sendVerifyCode(String phone);
    @PostMapping("register")
    Void register( User user, @RequestParam("code") String code);
    @GetMapping("check/{data}/{type}")
    Boolean checkUserData(@PathVariable("data") String data, @PathVariable("type") Integer type);
}
