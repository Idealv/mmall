package com.joe.user.server.service.impl;

import com.joe.user.common.Const;
import com.joe.user.common.ServerResponse;
import com.joe.user.server.domain.User;
import com.joe.user.server.dto.UserDTO;
import com.joe.user.server.repository.UserRepository;
import com.joe.user.server.service.UserService;
import com.joe.user.server.utils.MD5Util;
import com.joe.user.server.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
//    @Override
//    public ServerResponse<User> login(String username, String password) {
//        return null;
//    }

    @Override
    public ServerResponse<Object> register(UserDTO userDTO) {
        ServerResponse<Object> isUsernameNotExist = this.checkValid(userDTO.getUsername(), Const.USERNAME);
        //存在相同username
        if (!isUsernameNotExist.isSuccess()){
            return isUsernameNotExist;
        }
        ServerResponse<Object> isEmailNotExist = this.checkValid(userDTO.getEmail(), Const.USERNAME);
        if (!isEmailNotExist.isSuccess()){
            return isEmailNotExist;
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<Object> checkValid(String val, String type) {
        if (StringUtils.isNotEmpty(type)){
            int resultCount;
            switch (type){
                case Const.USERNAME:{
                    resultCount = userRepository.countByUsername(val);
                    return resultCount > 0 ?
                            ServerResponse.createByErrorMessage("此用户已存在") :
                            ServerResponse.createBySuccessMessage("参数校验成功");//没有相同username
                }
                case Const.EMAIL:{
                    resultCount = userRepository.countByEmail(val);
                    return resultCount > 0 ?
                            ServerResponse.createByErrorMessage("此邮箱已存在") :
                            ServerResponse.createBySuccessMessage("参数校验成功");
                }
                default:
                    return ServerResponse.createByErrorMessage("请传入正确的参数");
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
    }

    @Override
    public ServerResponse<Object> selectQuestion(String username) {
        ServerResponse<Object> isUsernameNotExist = this.checkValid(username, Const.USERNAME);
        //不存在则返回true
        if (isUsernameNotExist.isSuccess()){
            ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userRepository.selectQuestionByUsername(username);
        return question != null ?
                ServerResponse.createBySuccess(question) :
                ServerResponse.createByErrorMessage("问题不存在");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        Integer result = userRepository.countByUsernameAndQuestionAndAnswer(username, question, answer);
        if (result>0){
            String forgetToken = UUID.randomUUID().toString();
            try {
                redisTemplate.opsForValue().set(Const.TOKEN_PREFIX+username, forgetToken, 12, TimeUnit.HOURS);
            } catch (Exception e) {
                log.error(e.getMessage());
                return ServerResponse.createByErrorMessage("token出现异常,请尝试重新获取");
            }
            return ServerResponse.createBySuccess(forgetToken);
        }else {
            return ServerResponse.createByErrorMessage("答案错误");
        }
    }

    //忘记密码的情况下,通过回答问题获取token,以此重置密码
    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("请携带forgetToken参数");
        }
        ServerResponse<Object> response = this.checkValid(username, Const.USERNAME);
        if (response.isSuccess()){
            return ServerResponse.createByErrorMessage("该用户不存在");
        }
        String tokenInRedis = redisTemplate.opsForValue().get(Const.TOKEN_PREFIX + username).toString();
        if (StringUtils.isBlank(tokenInRedis)){
            return ServerResponse.createByErrorMessage("token无效或已过期");
        }
        if (StringUtils.equals(tokenInRedis,forgetToken)){
            String encodePassword = MD5Util.MD5EncodeUtf8(passwordNew);
            Integer result = userRepository.updatePasswordByUsername(username, encodePassword, new Date());
            return result > 0 ?
                    ServerResponse.createBySuccessMessage("密码重置成功") :
                    ServerResponse.createByErrorMessage("密码重置失败");
        }else {
            return ServerResponse.createByErrorMessage("token不匹配,请重新获取");
        }
    }

    //用新密码覆盖旧密码
    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, Integer id) {
        Integer result = userRepository.countByPasswordAndId(passwordOld, id);
        if (result==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        Integer updateResult = userRepository.updatePasswordById(
                MD5Util.MD5EncodeUtf8(passwordNew),
                id, new Date());

        if (updateResult==0){
            return ServerResponse.createByErrorMessage("密码修改失败");
        }
        return ServerResponse.createBySuccessMessage("密码修改成功");
    }

    //TODO: implement updateUserInfo operation

    @Override
    public ServerResponse<UserInfoVO> getInformation(Integer userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (!optional.isPresent()){
            return ServerResponse.createByErrorMessage("查找用户信息失败");
        }

        User user = optional.get();
        if (user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return ServerResponse.createBySuccess(userInfoVO);
    }
}
