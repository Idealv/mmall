package com.joe.category.client;

import com.joe.user.common.ServerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "category")
public interface CategoryClient {
    @GetMapping("/{categoryId}")
    ServerResponse<List<Integer>> getCategoryIdList(@PathVariable Integer categoryId);
}
