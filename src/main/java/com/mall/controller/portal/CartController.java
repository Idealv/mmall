package com.mall.controller.portal;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.ICartService;
import com.mall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVO> add(HttpSession session, Integer productId, Integer count) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(u.getId(), productId, count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVO> update(HttpSession session, Integer productId, Integer count) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(u.getId(), productId, count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVO> deleteProduct(HttpSession session, Integer userId, String productIds) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.delete(userId, productIds);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVO> list(HttpSession session) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(u.getId());
    }

    //全选
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> selectAll(HttpSession session) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(u.getId(), null, Const.Cart.CHECKED);
    }

    //全反选
    @RequestMapping("unselect_all.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelectAll(HttpSession session) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(u.getId(), null, Const.Cart.UN_CHECKED);
    }

    //单选
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVO> select(HttpSession session, Integer productId) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(u.getId(), productId, Const.Cart.CHECKED);
    }

    //单反选
    @RequestMapping("unSelect.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelect(HttpSession session, Integer productId) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(u.getId(), productId, Const.Cart.UN_CHECKED);
    }

    //查询用户购物车中产品数量
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(u.getId());
    }
}
