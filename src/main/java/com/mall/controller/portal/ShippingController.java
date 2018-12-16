package com.mall.controller.portal;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.Shipping;
import com.mall.pojo.User;
import com.mall.service.IShippingService;
import com.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;
    @Autowired
    private IUserService iUserService;
    
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest request, Shipping shipping) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iShippingService.add(u.getId(), shipping);
        }
        return response;
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpServletRequest request, Integer shippingId) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iShippingService.del(u.getId(), shippingId);
        }
        return response;
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest request, Shipping shipping) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iShippingService.update(u.getId(), shipping);
        }
        return response;
}

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpServletRequest request, Integer shippingId) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iShippingService.del(u.getId(), shippingId);
        }
        return response;
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest request,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        ServerResponse response = iUserService.checkLogin(request);
        if (response.isSuccess()){
            User u= (User) response.getData();
            return iShippingService.list(u.getId(), pageNum, pageSize);
        }
        return response;
    }
}
