package com.joe.common;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    @Autowired
    private RedisTemplate redisTemplate;

    private final SecurityConfig config;

    public JwtUsernamePasswordAuthenticationFilter(SecurityConfig config, AuthenticationManager authManager) {
        //match url: POST /login
        super(new AntPathRequestMatcher(config.getUrl(), "POST"));
        setAuthenticationManager(authManager);
        this.config = config;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse rsp)
            throws AuthenticationException, IOException {
        //验证用户账号密码,验证通过则返回jwt token
        User u = new User(obtainUsername(req), obtainPassword(req));
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                u.getUsername(), u.getPassword()
        ));
    }

    private String obtainUsername(HttpServletRequest req) {
        return req.getParameter("username");
    }

    private String obtainPassword(HttpServletRequest req) {
        return req.getParameter("password");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse rsp, FilterChain chain,
                                            Authentication auth) {

        Instant now = Instant.now();

        String token = Jwts.builder()
                .setSubject(auth.getName())//用户名
                .claim("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))//权限
                .setIssuedAt(Date.from(now))//签发时间
                .setExpiration(Date.from(now.plusSeconds(config.getExpiration())))//失效时间
                .signWith(SignatureAlgorithm.HS256, config.getSecret().getBytes())//签发规则
                .compact();
        rsp.addHeader(config.getHeader(), config.getPrefix() + " " + token);

        //response Header
        //Authorization: Bearer XXXX(jwt token)
    }

    @Getter
    @Setter
    private class User {
        private String username;

        private String password;

        public User() {
        }

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}