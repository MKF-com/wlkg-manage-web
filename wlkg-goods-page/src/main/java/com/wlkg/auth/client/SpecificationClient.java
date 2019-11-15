package com.wlkg.auth.client;

import com.wlkg.api.SpecificationAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface SpecificationClient extends SpecificationAPI {
}
