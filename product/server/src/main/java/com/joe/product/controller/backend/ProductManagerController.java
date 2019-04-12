package com.joe.product.controller.backend;

import com.joe.product.domain.Product;
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
@RequestMapping("/manager")
public class ProductManagerController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ServerResponse<Page<ProductListVO>> searchProduct(
            @RequestParam(value = "productName",defaultValue = "") String productName,
            @RequestParam(value = "pageNum",defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
            @RequestParam(value = "orderBy",defaultValue = "") String orderBy
    ){
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, Sort.by(orderBy));
        return productService.searchProduct(productName, pageRequest);
    }

    @GetMapping("/")
    public ServerResponse<Page<Product>> getProductList(
            @RequestParam(value = "pageNum",defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize
    ){
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        return productService.getProductList(pageRequest);
    }

    @GetMapping("/{productId}")
    public ServerResponse<ProductDetailVO> getDetail(@PathVariable Integer productId){
        return productService.manageProductDetail(productId);
    }

    //更新saleStatus:满足部分更新
    @PatchMapping("/product/{prodcutId}")
    public ServerResponse<String> updateSaleStatus(
            @PathVariable Integer productId,
            @RequestParam(value = "saleStatus",defaultValue = "") Integer saleStatus
    ){
        return productService.updateSaleStatus(productId, saleStatus);
    }

    @PutMapping
    public ServerResponse<String> saveOrUpdateProduct(Product product){
        return productService.saveOrUpdateProduct(product);
    }
}
