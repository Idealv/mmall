package com.joe.user.server.service.impl;

import com.joe.user.common.Const;
import com.joe.user.common.ServerResponse;
import com.joe.user.server.dto.UserDTO;
import com.joe.user.server.service.UserService;
import com.joe.user.server.vo.UserInfoVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Test
    public void register() {
        UserDTO user= new UserDTO();
        user.setUsername("testRegister");
        user.setPassword("123456");
        user.setEmail("test@mall.com");

        ServerResponse<Object> response = userService.register(user);
        assertTrue(response!=null);
    }

    @Test
    public void checkValid() {
        ServerResponse<Object> response = userService.checkValid("admin", Const.USERNAME);
        assertTrue(response != null);
    }

    @Test
    public void selectQuestion() {
        ServerResponse<Object> response = userService.selectQuestion("admin");
        assertTrue(response.isSuccess());
    }

    @Test
    public void checkAnswer() {
        ServerResponse<String> response = userService.checkAnswer("test", "question", "answer");
        assertTrue(response.isSuccess());
    }

    @Test
    public void forgetResetPassword() {
        ServerResponse<String> response = userService.forgetResetPassword(
                "test",
                "111111",
                "f8be97ab-2e3f-4c80-a6ee-9052dd375ca5");
        assertTrue(response.isSuccess());
    }

    @Test
    public void resetPassword() {
        ServerResponse<String> response = userService.resetPassword(
                "12345",
                "111111",
                24);
        assertTrue(response.isSuccess());
    }

//    @Test
//    public void updateInformation() {
//    }

    @Test
    public void getInformation() {
        ServerResponse<UserInfoVO> response = userService.getInformation(24);
        UserInfoVO userInfoVO = response.getData();
        assertTrue(userInfoVO != null);
    }

//    @Test
//    public void checkAdminRole() {
//    }
}