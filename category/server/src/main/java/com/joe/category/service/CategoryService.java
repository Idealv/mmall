package com.joe.category.service;

import com.joe.category.domain.Category;
import com.joe.user.common.ServerResponse;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    ServerResponse<List<Integer>> getDeepCategoryAndChildren(Integer categoryId);

    ServerResponse<Object> addCategory(String categoryName, Integer parentId);

    ServerResponse<Object> updateCategoryName(String categoryName, Integer categoryId);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId);

    Set<Category> getChildrenCategory(Set<Category> categorySet, Integer parentCategoryId);
}
