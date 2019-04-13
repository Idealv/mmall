package com.joe.category.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "mmall_category")
@Getter
@Setter
@ToString
@Proxy(lazy = false)
public class Category {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * 因为jpa自身的原因,如果将最顶层的种类id设置为0,而表中有没有id为
     * 0的表项则会报错,所以将顶层种类父类id置为null
     */
    @ManyToOne
    @JoinColumn(name = "parent_id",referencedColumnName = "id")
    private Category parentCategory;

    /**
     * 种类名称
     */
    private String name;

    /**
     * 种类状态1-正常,2-已废弃
     */
    private Integer status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;

//    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
//    private Set<Product> products;
}
