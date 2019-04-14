package com.joe.category.client;

import com.joe.user.common.ServerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "category")
@RequestMapping("/manager")
public interface CategoryClient {
    @GetMapping("/{categoryId}")
    ServerResponse<List<Integer>> getCategoryIdList(@PathVariable Integer categoryId);
}
