package com.joe.common;


import lombok.Getter;

@Getter
public class SecurityConfig {
    private String url = "/login";

    private String header = "Authorization";

    private String prefix = "Bearer";

    private int expiration = 84600; // default 24 hours

    private String secret = "joe";
}
