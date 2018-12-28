package com.mall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IOrderService;
import com.mall.service.IUserService;
import com.mall.vo.OrderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {
    private Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private IUserService iUserService;

    //alipay
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse<OrderVO> pay(long orderNo, HttpServletRequest request){
        ServerResponse serverResponse = iUserService.checkLogin(request);
        String path = request.getSession().getServletContext().getRealPath("upload");
        if (serverResponse.isSuccess()){
            User u = (User) serverResponse.getData();
            return iOrderService.pay(u.getId(), orderNo, path);
        }
        return serverResponse;
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
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest request, Integer orderNo){
        ServerResponse userRes = iUserService.checkLogin(request);
        if (userRes.isSuccess()){
            User u = (User) userRes.getData();
            ServerResponse orderRes = iOrderService.queryOrderPayStatus(u.getId(), orderNo);
            if (orderRes.isSuccess()){
                return ServerResponse.createBySuccess(true);
            }
        }
        return ServerResponse.createBySuccess(false);
    }

    //user order
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse<OrderVO> create(HttpServletRequest request, Integer shippingId) {
        ServerResponse serverResponse = iUserService.checkLogin(request);
        if (serverResponse.isSuccess()){
            User u= (User) serverResponse.getData();
            return iOrderService.createOrder(u.getId(), shippingId);
        }
        return serverResponse;
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse<String> getOrderCartProduct(HttpServletRequest request){
        ServerResponse serverResponse = iUserService.checkLogin(request);
        if (serverResponse.isSuccess()){
            User u= (User) serverResponse.getData();
            return iOrderService.getOrderCartProduct(u.getId());
        }
        return serverResponse;
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse<String> cancel(HttpServletRequest request,long orderNo){
        ServerResponse serverResponse = iUserService.checkLogin(request);
        if (serverResponse.isSuccess()) {
            User u= (User) serverResponse.getData();
            return iOrderService.cancelOrder(u.getId(), orderNo);
        }
        return serverResponse;
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpServletRequest request,long orderNo){
        ServerResponse serverResponse = iUserService.checkLogin(request);
        if (serverResponse.isSuccess()) {
            User u= (User) serverResponse.getData();
            return iOrderService.getDetail(u.getId(), orderNo);
        }
        return serverResponse;
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest request,
                               @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        ServerResponse serverResponse = iUserService.checkLogin(request);
        if (serverResponse.isSuccess()) {
            User u= (User) serverResponse.getData();
            return iOrderService.getOrderList(u.getId(), pageNum, pageSize);
        }
        return serverResponse;
    }
}
