package com.joe.product.repository.predicate;

import com.joe.product.domain.QProduct;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import java.util.List;

public class ProductPredicate {
    public static Predicate createPredicate(String productName, List<Integer> categoryIdList){
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();
        if (productName!=null){
            builder.and(product.name.like(productName));
        }
        if (categoryIdList!=null&&categoryIdList.size()>0){
            builder.and(product.category.id.in(categoryIdList));
        }
        return builder;
    }
}
