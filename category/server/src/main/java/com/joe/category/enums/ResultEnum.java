package com.joe.category.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {
    SUCCESS("成功"),
    FAILURE("失败")
    ;

    private String result;

    ResultEnum(String result) {
        this.result = result;
    }
}
