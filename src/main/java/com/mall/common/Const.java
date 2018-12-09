package com.mall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "CURRENT_USER";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    public interface Cart{
        int CHECKED=1;
        int UN_CHECKED=0;

        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
        String LIMIT_NUM_FAILED = "LIMIT_NUM_FAILED";
    }

    public interface Role{
        int ROLE_CUSTOMER=0;
        int ROLE_ADMIN=1;
    }

    public interface ProductListOrderby{
        Set<String> PRICE_ASC_DESC= Sets.newHashSet("price_desc","price_asc");
    }

    public enum PRODUCT_STATUS{
        ON_SALE(1,"on_sale");

        private Integer code;
        private String value;

        PRODUCT_STATUS(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        public Integer getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }
}
