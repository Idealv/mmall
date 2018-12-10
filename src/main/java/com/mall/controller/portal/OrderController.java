package com.mall.controller.portal;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IOrderService;
import com.mall.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse<OrderVO> create(HttpSession session, Integer shippingId){
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(u.getId(), shippingId);
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse<String> getOrderCartProduct(HttpSession session){
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(u.getId());
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse<String> cancel(HttpSession session,long orderNo){
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancelOrder(u.getId(), orderNo);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session,long orderNo){
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getDetail(u.getId(), orderNo);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(u.getId(), pageNum, pageSize);
    }
}
