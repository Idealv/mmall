package com.joe.category.service.impl;

import com.joe.category.service.CategoryService;
import com.joe.user.common.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceImplTest {
    @Autowired
    private CategoryService categoryService;

    @Test
    public void getDeepCategoryAndChildren() {
        ServerResponse<List<Integer>> deepCategoryAndChildren = categoryService.getDeepCategoryAndChildren(100001);

        assertTrue(deepCategoryAndChildren.getData() != null);
    }
}