package com.joe.product.service.impl;

import com.joe.product.ProductApplicationTests;
import com.joe.product.domain.Product;
import com.joe.product.repository.ProductRepository;
import com.joe.product.vo.ProductDetailVO;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductServiceImplTest extends ProductApplicationTests {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testDeepCopy() throws Exception {
        Product product = productRepository.getOne(26);
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
        BeanUtils.copyProperties(product, productDetailVO);
        System.out.println(productDetailVO);
    }
}