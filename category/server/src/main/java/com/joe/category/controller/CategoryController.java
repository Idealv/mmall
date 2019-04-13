package com.joe.category.controller;

import com.joe.category.service.CategoryService;
import com.joe.user.common.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{categoryId}")
    public ServerResponse<List<Integer>> getCategoryIdList(
            @PathVariable  Integer categoryId) {
        return categoryService.getDeepCategoryAndChildren(categoryId);
    }
}
