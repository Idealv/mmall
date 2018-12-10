package com.mall.common;

import com.google.common.collect.Sets;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;

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

    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");


        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static String getDescByCode(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum.getValue();
                }
            }
            throw new RuntimeException("么有找到对应的枚举");
        }
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

        public String getDescByCode(Integer code){
            for (PRODUCT_STATUS product_status:values()) {
                if (product_status.getCode()==code){
                    return product_status.getValue();
                }
            }
            throw new RuntimeException("没有找到对应的枚举项");
        }
    }

    public enum PaymentType{
        ONLINE_PAY(1, "在线支付");

        private Integer code;
        private String value;

        PaymentType(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        public Integer getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static String getDescByCode(Integer code){
            for (PaymentType paymentType:values()) {
                if (paymentType.getCode()==code){
                    return paymentType.getValue();
                }
            }
            throw new RuntimeException("没有找到对应的枚举项");
        }
    }
}
