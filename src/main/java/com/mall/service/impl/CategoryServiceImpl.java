package com.mall.service.impl;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mall.common.ServerResponse;
import com.mall.dao.CategoryMapper;
import com.mall.pojo.Category;
import com.mall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类失败,参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);

        int resultCount = categoryMapper.insert(category);
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("添加品类成功");
        }
        return ServerResponse.createBySuccessMessage("添加品类失败");
    }

    @Override
    public ServerResponse<String> updateCategoryName(String categoryName, Integer categoryId) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("更新品类失败,参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);

        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("更新品类成功");
        }
        return ServerResponse.createBySuccessMessage("更新品类失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId) {
        List<Category> categoryList = categoryMapper.selCategoryChildrenBypId(parentId);
        if (CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到子节点");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


    @Override
    public ServerResponse<List<Integer>> getDeepCategoryAndChildren(Integer categoryId) {
        HashSet<Category> newCategorySet = Sets.newHashSet();
        Set<Category> categorySet = findChildCategory(newCategorySet, categoryId);
        List<Integer> list = Lists.newArrayList();
        for (Category category:
             categorySet) {
            list.add(category.getId());
        }
        return ServerResponse.createBySuccess(list);
    }


    public Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selCategoryChildrenBypId(categoryId);
        for (Category c:
             categoryList) {
            findChildCategory(categorySet, c.getId());
        }
        return categorySet;
    }
}
