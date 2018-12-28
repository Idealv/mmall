package com.mall.common.interceptor;

import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.util.CookieUtil;
import com.mall.util.JsonUtil;
import com.mall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthrityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                     HttpServletResponse response, Object o) throws Exception {
        log.info("preHandle");
        HandlerMethod handlerMethod= (HandlerMethod) o;
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getName();

        StringBuffer reqParams = new StringBuffer();
        Map paramMap = httpServletRequest.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        if (it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            String mapKey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;

            Object obj=entry.getValue();
            if (obj instanceof String[]){
                String[] strs = (String[]) obj;
                mapValue = Arrays.toString(strs);
            }
            reqParams.append(mapKey).append("=").append(mapValue);
        }

        if (StringUtils.equals(className, "UserManageController") &&
                StringUtils.equals(methodName, "login")) {
            log.info("拦截器拦截到请求,className:{},methodName:{}", className, methodName);
            return true;
        }

        User user=null;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)){
            String userJson = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.parse(userJson, User.class);
        }
        //非登陆方法都会把methodName,className,reqParams打印出来
        log.info("className:{},methodName:{},params:{}", className, methodName, reqParams);

        if (user==null||(user.getRole().intValue()!= Const.Role.ROLE_ADMIN)){
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();

            //富文本特殊处理
            if (user==null){
                if (StringUtils.equals(className, "ProductManageController") &&
                        StringUtils.equals(methodName, "richTextImgUpload")){
                    Map resultMap= Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请登录管理员");
                    writer.print(JsonUtil.stringify(resultMap));
                }else {

                    writer.print(JsonUtil.stringify(ServerResponse.createByErrorMessage("拦截器拦截,用户未登录")));
                }
            }else {
                if (StringUtils.equals(className, "ProductManageController") &&
                        StringUtils.equals(methodName, "richTextImgUpload")){
                    Map resultMap= Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限");
                    writer.print(JsonUtil.stringify(resultMap));
                }
                writer.print(JsonUtil.stringify(ServerResponse.createByErrorMessage("拦截器拦截,需要管理员权限")));
            }
            writer.flush();
            writer.close();
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
