package com.mall.controller.portal;


import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.service.IProductService;
import com.mall.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVO> detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(
            @RequestParam(value = "keyword",required = false)String keyword,
            @RequestParam(value = "categoryId",required = false)Integer categoryId,
            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iProductService.getProductBykeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
