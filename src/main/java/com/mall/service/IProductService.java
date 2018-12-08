package com.mall.service;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.pojo.Product;
import com.mall.vo.ProductDetailVO;

public interface IProductService {
    ServerResponse<String> saveOrUpdateProduct(Product product);

    ServerResponse<String> updateSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVO> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName, int productId, int pageNum, int pageSize);

    ServerResponse<ProductDetailVO> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductBykeywordCategory(String keyword, Integer categoryId,
                                                         Integer pageNum, Integer pageSize,
                                                         String orderBy);
}
