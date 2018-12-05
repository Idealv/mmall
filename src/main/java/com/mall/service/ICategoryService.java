package com.mall.service;

import com.mall.common.ServerResponse;
import com.mall.pojo.Category;

import java.util.List;
import java.util.Set;

public interface ICategoryService {
    ServerResponse<String> addCategory(String categoryName,Integer parentId);
    ServerResponse<String> updateCategoryName(String categoryName,Integer categoryId);
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId);
    ServerResponse<List<Integer>> getDeepCategoryAndChildren(Integer categoryId);
    Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId);
}
