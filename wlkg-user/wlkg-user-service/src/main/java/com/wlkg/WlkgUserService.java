package com.wlkg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.wlkg.auth.mapper")
public class WlkgUserService {
    public static void main(String[] args) {
        SpringApplication.run(WlkgUserService.class, args);
    }
}
