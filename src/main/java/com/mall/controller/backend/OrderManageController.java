package com.mall.controller.backend;


import com.github.pagehelper.PageInfo;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IOrderService;
import com.mall.service.IUserService;
import com.mall.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session,
                                         @RequestParam(value = "pageNum",defaultValue = "0")Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "该用户未登录,强制登录");
        }
        if (iUserService.checkAdminRole(currentUser).isSuccess()) {
            return iOrderService.manageGetOrderList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作,需管理员权限");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVO> detail(HttpSession session, long orderNo){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "该用户未登录,强制登录");
        }
        if (iUserService.checkAdminRole(currentUser).isSuccess()) {
            return iOrderService.manageDetail(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作,需管理员权限");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpSession session, long orderNo,
                                           @RequestParam(value = "pageNum",defaultValue = "0")Integer pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "该用户未登录,强制登录");
        }
        if (iUserService.checkAdminRole(currentUser).isSuccess()) {
            return iOrderService.manageSearch(pageNum, pageSize, orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作,需管理员权限");
        }
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<PageInfo> sendGoods(HttpSession session, long orderNo){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "该用户未登录,强制登录");
        }
        if (iUserService.checkAdminRole(currentUser).isSuccess()) {
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作,需管理员权限");
        }
    }
}
