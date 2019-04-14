package com.joe.category.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joe.category.domain.Category;
import com.joe.category.enums.OperationEnum;
import com.joe.category.enums.ResultEnum;
import com.joe.category.utils.MessageBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.derby.iapi.util.StringUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testBuilder(){
        List<Category> categoryList = categoryRepository.findAllByParentCategoryId(100001);
        assertTrue(categoryList!=null);
    }


    @Test
    public void splitStr(){
        Map<String, String> resultMap = Maps.newHashMap();

        String sourceStr = "getTest.address: 不能为空, getTest.name: 不能为空";

        if (sourceStr.contains(",")){
            List<String> msgs = Lists.newArrayList();
            String[] split = sourceStr.split(",");
            //getTest.address: 不能为空

            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].substring(split[i].indexOf(".")+1);
            }

            System.out.println(StringUtils.join(split,","));
        }else {
            sourceStr.substring(sourceStr.indexOf("."));
        }

    }

}