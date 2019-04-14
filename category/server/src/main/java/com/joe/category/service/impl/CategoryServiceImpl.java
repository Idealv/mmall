package com.joe.category.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.joe.category.domain.Category;
import com.joe.category.enums.OperationEnum;
import com.joe.category.enums.ResultEnum;
import com.joe.category.repository.CategoryRepository;
import com.joe.category.service.CategoryService;
import com.joe.category.utils.MessageBuilder;
import com.joe.user.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;


@Service
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    private MessageBuilder builder = new MessageBuilder("种类");

    @Override
    public ServerResponse<List<Integer>> getDeepCategoryAndChildren(Integer categoryId) {
        if (categoryId==null){
            return ServerResponse.createByErrorMessage(
                    builder.searchFailure()
            );
        }

        Set<Category> categorySet = getChildrenCategory(Sets.newHashSet(), categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();

        for (Category c:
                categorySet) {
            categoryIdList.add(c.getId());
        }

        return ServerResponse.createBySuccess(categoryIdList);
    }

    @Override
    public ServerResponse<Object> addCategory(String categoryName, Integer parentId) {
        Category category = new Category();
        Category parentCategory = categoryRepository.getOne(parentId);

        if (parentCategory==null){
            return ServerResponse.createByErrorMessage(
                    builder.searchFailure()
            );
        }

        category.setParentCategory(parentCategory);
        category.setName(categoryName);

        try {
            categoryRepository.save(category);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ServerResponse.createByErrorMessage(
                    builder.saveFailure()
            );
        }

        return ServerResponse.createBySuccessMessage(
                builder.saveSuccess()
        );
    }

    @Override
    public ServerResponse<Object> updateCategoryName(String categoryName, Integer categoryId) {
        Category category = categoryRepository.getOne(categoryId);
        if (category==null){
            return ServerResponse.createByErrorMessage(
                    builder.searchFailure()
            );
        }

        category.setName(categoryName);
        try {
            categoryRepository.save(category);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ServerResponse.createByErrorMessage(builder.searchFailure());
        }

        return ServerResponse.createBySuccessMessage(
                builder.searchSuccess()
        );
    }

    //只查询当前子种类
    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId) {
        List<Category> childCategoryList = categoryRepository.findAllByParentCategoryId(parentId);
        if (!(childCategoryList.size()>0)){
            return ServerResponse.createByErrorMessage("未找到子节点");
        }
        return ServerResponse.createBySuccess(childCategoryList);
    }


    //查找当前种类以及种类的子种类(包括子种类的子种类,可能嵌套n层)
    @Override
    public Set<Category> getChildrenCategory(Set<Category> categorySet, Integer parentCategoryId) {
        Category parent = categoryRepository.getOne(parentCategoryId);
        if (parent != null) {
            categorySet.add(parent);
        }

        List<Category> childCategoryList = categoryRepository.findAllByParentCategoryId(parentCategoryId);

        for (Category category :
                childCategoryList) {
            getChildrenCategory(categorySet, category.getId());
        }

        return categorySet;
    }
}
