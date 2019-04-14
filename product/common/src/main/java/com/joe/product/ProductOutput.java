package com.joe.product;


import java.math.BigDecimal;


public class ProductOutput {
    private Integer productId;

    private String productName;

    private String productSubtitle;

    private String productMainImage;

    private String productSubImages;

    private BigDecimal productPrice;

    private Integer productStock;

    /**
     * 商品状态.1-在售 2-下架 3-删除
     */
    private Integer productStatus;
}
