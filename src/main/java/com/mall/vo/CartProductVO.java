package com.mall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter@Setter
public class CartProductVO {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    private BigDecimal productTotalPrice;
    private Integer productStock;
    private Integer productChecked;

    private String limitQuantity;
}
