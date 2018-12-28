package com.mall.service;

import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.servlet.http.HttpServletRequest;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String val,String type);
    ServerResponse<String> selectQuestion(String username);
    ServerResponse<String> checkAnswer(String username,String question,String answer);
    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);
    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User u);
    ServerResponse<User> updateInformation(User u);
    ServerResponse<User> getInformation(Integer userId);
    ServerResponse<String> checkAdminRole(User u);
    ServerResponse alertNoLogin();
    ServerResponse checkLogin(HttpServletRequest request);
}
