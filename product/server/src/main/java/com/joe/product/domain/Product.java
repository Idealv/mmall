package com.joe.product.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Getter
@Setter
@ToString
@Table(name = "mmall_product")
@Proxy(lazy = false)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 分类id
     */
    //@ManyToOne
    //@JoinColumn(name = "category_id",referencedColumnName = "id")
    //private Category category;
    //一个产品对应一个种类,一个种类
    @Column(name = "category_id")
    private Integer categoryId;


    private String name;

    private String subtitle;


    /**
     * 主图
     */
    private String mainImage;

    /**
     * 副图
     */
    private String subImages;

    /**
     * 详情
     */
    private String detail;

    private BigDecimal price;

    private Integer stock;

    /**
     * 商品状态.1-在售 2-下架 3-删除
     */
    private Integer status;

    private Date createTime;

    private Date updateTime;
}
