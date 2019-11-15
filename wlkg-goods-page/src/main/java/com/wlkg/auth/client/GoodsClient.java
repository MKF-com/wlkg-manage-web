package com.wlkg.auth.client;

import com.wlkg.api.GoodsAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsAPI {
}
