package com.joe.category.enums;

import lombok.Getter;

@Getter
public enum OperationEnum {
    UPDATE("更新"),
    DELETE("删除"),
    SAVE("保存"),
    SEARCH("查询"),
    ;

    private String operation;

    OperationEnum(String operation) {
        this.operation = operation;
    }
}
