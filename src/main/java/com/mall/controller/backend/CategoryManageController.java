package com.mall.controller.backend;

import com.mall.common.ServerResponse;
import com.mall.pojo.Category;
import com.mall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpServletRequest request, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iCategoryService.addCategory(categoryName, parentId);
//        }
//        return serverResponse;
        return iCategoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpServletRequest request, String categoryName, Integer categoryId) {
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iCategoryService.updateCategoryName(categoryName, categoryId);
//        }
//        return serverResponse;
        return iCategoryService.updateCategoryName(categoryName, categoryId);
    }

    @RequestMapping(value = "get_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpServletRequest request,
                                                                      @RequestParam(value = "categoryId"
                                                                              , defaultValue = "0") Integer categoryId) {
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iCategoryService.getChildrenParallelCategory(categoryId);
//        }
//        return serverResponse;
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(HttpServletRequest request,
                                                         @RequestParam(value = "categoryId",
                                                                 defaultValue = "0") Integer categoryId) {
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iCategoryService.getDeepCategoryAndChildren(categoryId);
//        }
//        return serverResponse;
        return iCategoryService.getDeepCategoryAndChildren(categoryId);
    }
}
