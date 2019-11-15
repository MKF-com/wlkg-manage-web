package com.wlkg.auth.client;

import com.wlkg.api.BrandAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "item-service")
public interface BrandClient extends BrandAPI {
}
