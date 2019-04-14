package com.joe.category.controller;

import com.joe.category.domain.Category;
import com.joe.category.service.CategoryService;
import com.joe.user.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Slf4j
@Validated
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{categoryId}")
    public ServerResponse<List<Integer>> getCategoryIdList(
            @PathVariable  Integer categoryId) {
        return categoryService.getDeepCategoryAndChildren(categoryId);
    }

    @PostMapping
    public ServerResponse<Object> addCategory(
            @RequestBody @NotNull String categoryName,
            @RequestBody @NotNull Integer parentId){
        return categoryService.addCategory(categoryName, parentId);
    }

//    GET：读取（Read）
//    POST：新建（Create）
//    PUT：更新（Update）
//    PATCH：更新（Update），通常是部分更新
//    DELETE：删除（Delete）
    @PutMapping
    public ServerResponse<Object> updateCategoryName(
            @RequestBody @NotNull String categoryName,
            @RequestBody @NotNull Integer categoryId){
        return categoryService.updateCategoryName(categoryName, categoryId);
    }
    
    @GetMapping
    public ServerResponse<List<Category>> getChildrenParallelCategory(
            @RequestParam @NotNull Integer parentId){
        return categoryService.getChildrenParallelCategory(parentId);
    }
}
