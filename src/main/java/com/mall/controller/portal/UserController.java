package com.mall.controller.portal;

import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import com.mall.util.CookieUtil;
import com.mall.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mall.util.RedisShardedPoolUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session,
                                      HttpServletResponse servletResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            //session只保存本次会话消息,jsessionid保存在的cookie在关闭浏览器后销毁，若要记住用户
            //需要使用token
            //todo 使用jwt实现记住用户
            //session.setAttribute(Const.CURRENT_USER, response.getData());
            CookieUtil.writeLoginToken(servletResponse, session.getId());
            //redis中保存的session失效时间为30分钟
            RedisShardedPoolUtil.setEx(session.getId(),
                    JsonUtil.stringify(response.getData()),
                    Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest request,HttpServletResponse response) {
        String loginToken = CookieUtil.readLoginToken(request);
        CookieUtil.removeLoginToken(request, response);
        RedisShardedPoolUtil.del(loginToken);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String val, String type) {
        return iUserService.checkValid(val, type);
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        ServerResponse response = iUserService.checkLogin(request);
        User u= (User) response.getData();
        if (response.isSuccess()){
            return ServerResponse.createBySuccess(u);
        }
        return response;
    }

    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    //忘记密码，重置密码的情况
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest request, String passwordOld, String passwordNew) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iUserService.resetPassword(passwordOld, passwordNew, u);
        }
        return response;
    }

    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpServletRequest request, User user) {
        ServerResponse userRes = iUserService.checkLogin(request);
        if (userRes.isSuccess()){
            User u= (User) userRes.getData();
            //防止横向越权,避免传入别的用户的id,而是使用session中保存的id
            user.setId(u.getId());
            //不能修改username,且保证更新session后仍有username属性
            user.setUsername(u.getUsername());
            ServerResponse<User> response = iUserService.updateInformation(u);
            if (response.isSuccess()) {
                String loginToken = CookieUtil.readLoginToken(request);
                RedisShardedPoolUtil.setEx(loginToken,
                        JsonUtil.stringify(response.getData()),
                        Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
            return response;
        }
        return userRes;

    }

    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest request) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iUserService.getInformation(u.getId());
        }
        return response;
    }
}
