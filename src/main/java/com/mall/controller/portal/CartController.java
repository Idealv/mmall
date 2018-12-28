package com.mall.controller.portal;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.ICartService;
import com.mall.service.IUserService;
import com.mall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;
    @Autowired
    private IUserService iUserService;
    
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVO> add(HttpServletRequest request, Integer productId, Integer count) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()) {
            User u= (User) response.getData();
            return iCartService.add(u.getId(), productId, count);
        }
        return response;
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVO> update(HttpServletRequest request, Integer productId, Integer count) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iCartService.update(u.getId(), productId, count);
        }
        return response;
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVO> deleteProduct(HttpServletRequest request, Integer userId, String productIds) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            return iCartService.delete(userId, productIds);
        }
        return response;
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVO> list(HttpServletRequest request) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iCartService.list(u.getId());
        }
        return response;
    }

    //全选
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> selectAll(HttpServletRequest request) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iCartService.selectOrUnselect(u.getId(), null, Const.Cart.CHECKED);
        }
        return response;
    }

    //全反选
    @RequestMapping("unselect_all.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelectAll(HttpServletRequest request) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iCartService.selectOrUnselect(u.getId(), null, Const.Cart.UN_CHECKED);
        }
        return response;
    }

    //单选
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVO> select(HttpServletRequest request, Integer productId) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iCartService.selectOrUnselect(u.getId(), productId, Const.Cart.CHECKED);
        }
        return response;
    }

    //单反选
    @RequestMapping("unSelect.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelect(HttpServletRequest request, Integer productId) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iCartService.selectOrUnselect(u.getId(), productId, Const.Cart.UN_CHECKED);
        }
        return response;
    }

    //查询用户购物车中产品数量
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest request) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iCartService.getCartProductCount(u.getId());
        }
        return ServerResponse.createBySuccess(0);
    }
}
