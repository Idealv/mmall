package com.mall.controller.backend;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.Category;
import com.mall.pojo.User;
import com.mall.service.ICategoryService;
import com.mall.service.IUserService;
import com.mall.util.CookieUtil;
import com.mall.util.JsonUtil;
import com.mall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
    public ServerResponse<String> addCategory(HttpServletRequest request, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        ServerResponse serverResponse = iUserService.checkRole(request);
        if (serverResponse.isSuccess()) {
            return iCategoryService.addCategory(categoryName, parentId);
        }
        return serverResponse;
    }

    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpServletRequest request, String categoryName, Integer categoryId) {
        ServerResponse serverResponse = iUserService.checkRole(request);
        if (serverResponse.isSuccess()) {
            return iCategoryService.updateCategoryName(categoryName, categoryId);
        }
        return serverResponse;
    }

    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpServletRequest request,
                                                                      @RequestParam(value = "categoryId"
                                                                              , defaultValue = "0") Integer categoryId) {
        ServerResponse serverResponse = iUserService.checkRole(request);
        if (serverResponse.isSuccess()) {
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }
        return serverResponse;
    }

    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(HttpServletRequest request,
                                                         @RequestParam(value = "categoryId",
                                                                 defaultValue = "0") Integer categoryId) {
        ServerResponse serverResponse = iUserService.checkRole(request);
        if (serverResponse.isSuccess()) {
            return iCategoryService.getDeepCategoryAndChildren(categoryId);
        }
        return serverResponse;
    }
}
