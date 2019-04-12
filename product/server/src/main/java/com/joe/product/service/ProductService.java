package com.joe.product.service;

import com.joe.product.domain.Product;
import com.joe.product.vo.ProductDetailVO;
import com.joe.product.vo.ProductListVO;
import com.joe.user.common.ServerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ServerResponse<String> saveOrUpdateProduct(Product product);

    ServerResponse<String> updateSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVO> manageProductDetail(Integer productId);

    ServerResponse<Page<Product>> getProductList(Pageable pageable);

    ServerResponse<Page<ProductListVO>> searchProduct(String productName, Pageable pageable);

    ServerResponse<ProductDetailVO> getProductDetail(Integer productId);

    ServerResponse<Page<ProductListVO>> getProductBykeywordCategory(String keyword, Integer categoryId,
                                                                    PageRequest pageRequest);
}
