package com.joe.user.server.controller;

import com.joe.user.common.ServerResponse;
import com.joe.user.server.domain.User;
import com.joe.user.server.dto.UserDTO;
import com.joe.user.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ServerResponse<Object> register(@Valid UserDTO userDTO, BindingResult result) {
//        ServerResponse<Object> checkResponse = ParamCheckUtil.checkParam(result);
//        if (!checkResponse.isSuccess()) {
//            return checkResponse;
//        }
        return userService.register(userDTO);
    }

    @GetMapping("/check_valid")
    public ServerResponse<Object> checkValid(
            @RequestParam @NotNull(message = "检查用户名不能能为空") String val,
            @RequestParam @NotNull(message = "类型不能为空") String type, BindingResult result) {
//        ServerResponse<Object> checkResponse = ParamCheckUtil.checkParam(result);
//        if (!checkResponse.isSuccess()) {
//            return checkResponse;
//        }
        return userService.checkValid(val, type);
    }

    @GetMapping("/{id}")
    public ServerResponse<Object> getUserInfo(@PathVariable Integer userId) {
        //TODO: 从redis中读数据(登录成功->通过username从数据库中查出User->写入redis) token中包含userId和username
        //从cookie中读取token->从cookie中读取 如果要适配移动端则需要通过参数传入

        //伪代码
        User user = new User();
        return ServerResponse.createBySuccess(user);
    }

    //获取重置密码的问题
    @GetMapping("/forget_get_question")
    public ServerResponse<Object> forgetGetQuestion(@RequestParam String username){
        return StringUtils.isNotBlank(username) ?
                userService.selectQuestion(username) :
                ServerResponse.createByErrorMessage("用户名不能为空");
    }

    @GetMapping("/check_answer")
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        return userService.checkAnswer(username, question, answer);
    }

    @GetMapping("/forget_reset_password")
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        return userService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    @GetMapping("/reset_password")
    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew) {
        //TODO: get id from jwt token what in cookie
        Integer id = null;
        return userService.resetPassword(passwordOld, passwordNew, id);
    }
}
