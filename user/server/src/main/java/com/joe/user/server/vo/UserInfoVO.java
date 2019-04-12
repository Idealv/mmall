package com.joe.user.server.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoVO {
    private Integer id;

    private String username;

    private String email;

    private String phone;

    private String question;
}
