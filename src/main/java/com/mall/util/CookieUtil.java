package com.mall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {
    private static final String COOKIE_DOMAIN = ".idealmall.com";
    private static final String COOKIE_NAME = "login_token";

    private CookieUtil() {
    }

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cks = request.getCookies();
        if (cks!=null){
            for (Cookie ck:
                 cks) {
                log.info("read cookie:name:{},value:{}", ck.getName(), ck.getValue());
                if (StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    return ck.getValue();
                }

            }
        }
        return null;
    }

    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie ck = new Cookie(COOKIE_NAME, token);
        ck.setPath("/");
        ck.setDomain(COOKIE_DOMAIN);
        ck.setHttpOnly(true);
        //一个月 60*60*24*30
        ck.setMaxAge(60 * 60 * 24 * 30);

        log.info("wirte cookie:name:{},value:{}", ck.getName(), ck.getValue());
        response.addCookie(ck);
    }

    public static void removeLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();
        if (cks!=null){
            for (Cookie ck:
                    cks) {
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setHttpOnly(true);
                    ck.setMaxAge(0);
                    log.info("remove cookie:name:{},value:{}", ck.getName(), ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }
}
