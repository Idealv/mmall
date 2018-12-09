package com.mall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter@Setter
public class CartVO {
    private List<CartProductVO> cartProductVOList;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked;
    private String imageHost;
}
