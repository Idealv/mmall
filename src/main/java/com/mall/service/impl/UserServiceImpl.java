package com.mall.service.impl;

import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.common.TokenCache;
import com.mall.dao.UserMapper;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import com.mall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //selectLogin By username and password
        User user = userMapper.selectLogin(username, MD5Util.MD5EncodeUtf8(password));
        if (user==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }

        //设置权限
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount=userMapper.insert(user);
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String val, String type) {
        if (StringUtils.isNotBlank(type)) {
            int resultCount;
            if (Const.USERNAME.equals(type)){
                resultCount = userMapper.checkUsername(val);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }

            if (Const.EMAIL.equals(type)){
                resultCount = userMapper.checkEmail(val);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }

        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
        //只有在用户名不存在时才返回true
        if (response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //返回token原因:防止用户横向越权,只有username,question,answer全部正确才返回token
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,需传入token");
        }
        ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
        if (response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或已过期");
        }
        if (StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (resultCount>0){
                return ServerResponse.createBySuccessMessage("密码修改成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token无效,请重新获取充值密码的token");
        }
        return ServerResponse.createByErrorMessage("密码修改失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User u) {
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), u.getId());
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        u.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(u);
        if (updateCount==0){
            return ServerResponse.createByErrorMessage("密码修改失败");
        }
        return ServerResponse.createBySuccessMessage("密码修改成功");
    }

    @Override
    public ServerResponse<User> updateInformation(User u) {
        int resultId = userMapper.checkEmailByUserId(u.getEmail(), u.getId());
        if (resultId==0){
            return ServerResponse.createByErrorMessage("email被占用,请使用别的邮箱");
        }
        User updateUser = new User();
        updateUser.setId(u.getId());
        updateUser.setEmail(u.getEmail());
        updateUser.setPassword(u.getPhone());
        updateUser.setQuestion(u.getQuestion());
        updateUser.setAnswer(u.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount>0){
            return ServerResponse.createBySuccess("用户信息更新成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("用户信息更新失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User u = userMapper.selectByPrimaryKey(userId);
        if (u==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        u.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(u);
    }

    @Override
    public ServerResponse<String> checkAdminRole(User u) {
        if (u!=null&&u.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
