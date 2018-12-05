package com.mall.controller.backend;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.Category;
import com.mall.pojo.User;
import com.mall.service.ICategoryService;
import com.mall.service.IUserService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
         User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
         if (currentUser==null){
             return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "该用户未登录,强制登录");
         }
         if (iUserService.checkAdminRole(currentUser).isSuccess()){
             return iCategoryService.addCategory(categoryName, parentId);
         }else {
             return ServerResponse.createByErrorMessage("无权限操作,需管理员权限");
         }
    }

    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session,String categoryName,Integer categoryId){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "该用户未登录,强制登录");
        }
        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            return iCategoryService.updateCategoryName(categoryName, categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需管理员权限");
        }
    }
    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session,
                                                                      @RequestParam(value = "categoryId"
                                                                              ,defaultValue = "0") Integer categoryId){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "该用户未登录,强制登录");
        }
        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需管理员权限");
        }
    }
    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(HttpSession session,@RequestParam(
            value = "categoryId", defaultValue = "0") Integer categoryId){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "该用户未登录,强制登录");
        }
        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            return iCategoryService.getDeepCategoryAndChildren(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需管理员权限");
        }
    }
}
