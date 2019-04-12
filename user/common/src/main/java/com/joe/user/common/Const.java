package com.joe.user.common;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class Const {
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String TOKEN_PREFIX="token_";

    public interface Role {
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    @Getter
    public enum PRODUCT_STATUS {
        ON_SALE(1, "on_sale"),
        ;

        private Integer code;

        private String msg;

        PRODUCT_STATUS(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    public interface ProductListOrderby {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }
}
