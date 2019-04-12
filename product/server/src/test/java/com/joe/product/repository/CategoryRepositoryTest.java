package com.joe.product.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.joe.product.ProductApplicationTests;
import com.joe.product.domain.Category;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class CategoryRepositoryTest extends ProductApplicationTests {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testGet() throws Exception{
        Category category = categoryRepository.getOne(100001);
        System.out.println(category);
    }

    @Test
    public void testFindAllByParentCategoryId() throws Exception{
        List<Category> categoryList = categoryRepository.findAllByParentCategoryId(100001);
        assertTrue(categoryList.size()>0);
    }
}