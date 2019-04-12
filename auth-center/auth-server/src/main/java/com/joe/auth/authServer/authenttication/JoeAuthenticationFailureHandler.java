package com.joe.auth.authServer.authenttication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joe.user.common.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("joeAuthenticationFailureHandler")
public class JoeAuthenticationFailureHandler  extends SimpleUrlAuthenticationFailureHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException e) throws IOException, ServletException {
        logger.info("login failed");
        //TODO add properties
        if ("json".equals("json")){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json;charset=UTF-8");
            //只向前台返回错误信息
            response.getWriter().write(objectMapper.writeValueAsString(ServerResponse.createByErrorMessage(e.getMessage())));
        }else {
            super.onAuthenticationFailure(request, response, e);
        }
    }
}
