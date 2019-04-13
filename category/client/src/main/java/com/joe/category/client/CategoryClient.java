package com.joe.category.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "category")
public interface CategoryClient {
    @GetMapping("/msg")
    String categoryMsg();
}
