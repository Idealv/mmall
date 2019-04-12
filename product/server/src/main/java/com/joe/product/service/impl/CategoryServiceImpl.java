package com.joe.product.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.joe.product.domain.Category;
import com.joe.product.repository.CategoryRepository;
import com.joe.product.service.CategoryService;
import com.joe.user.common.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ServerResponse<List<Integer>> getDeepCategoryAndChildren(Integer categoryId) {
        if (categoryId==null){
            return ServerResponse.createByErrorMessage("查询错误请重试");
        }

        Set<Category> categorySet = getChildrenCategory(Sets.newHashSet(), categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();

        for (Category c:
                categorySet) {
            categoryIdList.add(c.getId());
        }

        return ServerResponse.createBySuccess(categoryIdList);
    }

    private Set<Category> getChildrenCategory(Set<Category> categorySet, Integer parentCategoryId) {
        Category parent = categoryRepository.getOne(parentCategoryId);
        if (parent!=null){
            categorySet.add(parent);
        }

        List<Category> childCategoryList = categoryRepository.findAllByParentCategoryId(parentCategoryId);

        for (Category category:
                childCategoryList) {
            getChildrenCategory(categorySet, category.getId());
        }

        return categorySet;
    }
}
