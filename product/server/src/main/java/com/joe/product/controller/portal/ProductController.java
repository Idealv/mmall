package com.joe.product.controller.portal;

import com.joe.product.service.ProductService;
import com.joe.product.vo.ProductDetailVO;
import com.joe.product.vo.ProductListVO;
import com.joe.user.common.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}")
    public ServerResponse<ProductDetailVO> getProduct(@PathVariable Integer productId){
        return productService.getProductDetail(productId);
    }

    @GetMapping("/category/{categoryId}")
    public ServerResponse<Page<ProductListVO>> searchByCategoryId(
            @PathVariable Integer categoryId,
            @RequestParam(value = "pageNum",defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
            @RequestParam(value = "orderBy",defaultValue = "") String orderBy
            ){
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, Sort.by(orderBy));
        return productService.getProductBykeywordCategory(null, categoryId, pageRequest);
    }


    @GetMapping("/keyword/{keyword}")
    public ServerResponse<Page<ProductListVO>> searchByKeyword(
            @PathVariable String keyword,
            @RequestParam(value = "pageNum",defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
            @RequestParam(value = "orderBy",defaultValue = "") String orderBy
    ){
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, Sort.by(orderBy));
        return productService.getProductBykeywordCategory(keyword, null, pageRequest);
    }
}
