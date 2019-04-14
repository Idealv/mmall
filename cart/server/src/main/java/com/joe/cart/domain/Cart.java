package com.joe.cart.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name = "mmall_cart")
@Proxy(lazy = false)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    //1=已勾选,0=未勾选
    private Integer checked;

    private Date createTime;

    private Date updateTime;
}
