package com.wlkg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WlkgUpload {
    public static void main(String[] args) {
        SpringApplication.run(WlkgUpload.class, args);
    }
}