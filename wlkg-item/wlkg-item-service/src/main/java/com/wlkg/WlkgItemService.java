package com.wlkg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(value = "com.wlkg.auth.mapper")
public class WlkgItemService {
    public static void main(String[] args) {
        SpringApplication.run(WlkgItemService.class, args);
    }
}
