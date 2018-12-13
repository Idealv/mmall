package com.mall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IOrderService;
import com.mall.vo.OrderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {
    private Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private IOrderService iOrderService;

    //alipay
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse<OrderVO> pay(HttpSession session, long orderNo, HttpServletRequest request){
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(u.getId(), orderNo, path);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();
        Map<String, String[]> parameterMap = request.getParameterMap();
        //封装params
        for (Iterator iterator = parameterMap.keySet().iterator(); iterator.hasNext(); ) {
            String name = (String) iterator.next();
            String[] values = parameterMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",
                params.get("sign"), params.get("trade_status"), params.toString());
        //验证回调正确性
        params.remove("sign_type");
        try {
            //rsa2验证
            boolean AlipayRsaCheckV2 = AlipaySignature.rsaCheckV2(params
                    , Configs.getAlipayPublicKey()
                    , "utf-8", Configs.getSignType());
            if (!AlipayRsaCheckV2){
                return ServerResponse.createByErrorMessage("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
        }

        //todo 验证各种数据
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if (serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;

    }

    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Integer orderNo){
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(u.getId(), orderNo);
        if (serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

    //user order
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
