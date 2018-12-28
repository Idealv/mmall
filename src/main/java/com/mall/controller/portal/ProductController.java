package com.mall.controller.portal;


import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.service.IProductService;
import com.mall.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/{productId}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVO> detailRESTful(@PathVariable Integer productId){
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("/keyword/{keyword}")
    @ResponseBody
    public ServerResponse<PageInfo> searchRESTful(
            @PathVariable(value = "keyword")String keyword,
            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iProductService.getProductBykeywordCategory(keyword, null, pageNum, pageSize, orderBy);
    }

    @RequestMapping("/category/{categoryId}")
    @ResponseBody
    public ServerResponse<PageInfo> searchRESTful(
            @PathVariable Integer categoryId,
            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iProductService.getProductBykeywordCategory(null, categoryId, pageNum, pageSize, orderBy);
    }
}
