package com.mall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter@Setter
public class OrderProductVO {
    private List<OrderItemVO> orderItemVOList;
    private BigDecimal prodcutTotalPrice;
    private String imageHost;
}
