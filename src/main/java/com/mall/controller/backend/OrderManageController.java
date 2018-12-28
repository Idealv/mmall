package com.mall.controller.backend;


import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.service.IOrderService;
import com.mall.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest request,
                                         @RequestParam(value = "pageNum",defaultValue = "0")Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iOrderService.manageGetOrderList(pageNum, pageSize);
//        }
//        return serverResponse;
        return iOrderService.manageGetOrderList(pageNum, pageSize);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVO> detail(HttpServletRequest request, long orderNo){
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iOrderService.manageDetail(orderNo);
//        }
//        return serverResponse;
        return iOrderService.manageDetail(orderNo);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpServletRequest request, long orderNo,
                                           @RequestParam(value = "pageNum",defaultValue = "0")Integer pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iOrderService.manageSearch(pageNum, pageSize, orderNo);
//        }
//        return serverResponse;
        return iOrderService.manageSearch(pageNum, pageSize, orderNo);

    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<PageInfo> sendGoods(HttpServletRequest request, long orderNo){
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iOrderService.manageSendGoods(orderNo);
//        }
//        return serverResponse;
        return iOrderService.manageSendGoods(orderNo);
    }
}
