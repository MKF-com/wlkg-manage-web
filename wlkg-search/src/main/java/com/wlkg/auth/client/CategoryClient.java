package com.wlkg.auth.client;

import com.wlkg.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "item-service")
public interface CategoryClient extends CategoryApi {
}
