package com.joe.product.service;

import com.joe.user.common.ServerResponse;

import java.util.List;

public interface CategoryService {
    ServerResponse<List<Integer>> getDeepCategoryAndChildren(Integer categoryId);

}
