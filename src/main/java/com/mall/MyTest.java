package com.mall;

import com.mall.dao.UserMapper;
import com.mall.pojo.User;
import com.mall.util.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class MyTest {
    @Autowired
    private UserMapper userMapper;
    @Test
    public void testUserMapper() {
        int resultCount = userMapper.checkEmail("admin@happymmall.com");
        System.out.println("resultCount = " + resultCount);
    }

    @Test
    public void testLogin(){
        User user = userMapper.selectLogin("admin", MD5Util.MD5EncodeUtf8("admin"));
        System.out.println(user.getUsername());
    }
}
