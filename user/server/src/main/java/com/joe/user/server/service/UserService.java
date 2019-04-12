package com.joe.user.server.service;

import com.joe.user.common.ServerResponse;
import com.joe.user.server.dto.UserDTO;
import com.joe.user.server.vo.UserInfoVO;

public interface UserService {
    ServerResponse<Object> register(UserDTO userDTO);
    ServerResponse<Object> checkValid(String val,String type);
    ServerResponse<Object> selectQuestion(String username);
    ServerResponse<String> checkAnswer(String username,String question,String answer);
    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);
    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,Integer id);
//    ServerResponse<User> updateInformation(UserUpdateDTO userUpdateDTO);
    ServerResponse<UserInfoVO> getInformation(Integer userId);
}
