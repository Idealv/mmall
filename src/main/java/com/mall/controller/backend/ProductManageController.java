package com.mall.controller.backend;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.Product;
import com.mall.pojo.User;
import com.mall.service.IFileService;
import com.mall.service.IProductService;
import com.mall.service.IUserService;
import com.mall.util.PropertiesUtil;
import com.mall.vo.ProductDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse<String> productSave(HttpSession session, Product product) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录");
        }
        if (iUserService.checkAdminRole(u).isSuccess()) {
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("无权限,需管理员权限");
        }
    }


    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse<String> setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录");
        }
        if (iUserService.checkAdminRole(u).isSuccess()) {
            return iProductService.updateSaleStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("无权限,需管理员权限");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVO> getDetail(HttpSession session, Integer productId) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录");
        }
        if (iUserService.checkAdminRole(u).isSuccess()) {
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("无权限,需管理员权限");
        }
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession session,
                                            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录");
        }
        if (iUserService.checkAdminRole(u).isSuccess()) {
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限,需管理员权限");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpSession session,
                                            String productName,int productId,
                                            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录");
        }
        if (iUserService.checkAdminRole(u).isSuccess()) {
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限,需管理员权限");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,
                                 @RequestParam(value = "upload_file",required = false)MultipartFile file,
                                 HttpServletRequest request) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录");
        }
        if (iUserService.checkAdminRole(u).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getPropery("ftp.server.http.prefix") + targetFileName;

            Map map = Maps.newHashMap();
            map.put("uri", targetFileName);
            map.put("url", url);
            return ServerResponse.createBySuccess(map);
        } else {
            return ServerResponse.createByErrorMessage("无权限,需管理员权限");
        }
    }

    @RequestMapping("richText_img_upload.do")
    @ResponseBody
    public Map richTextImgUpload(HttpSession session,
                                 @RequestParam(value = "upload_file",required = false) MultipartFile file,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        Map resultMap=Maps.newHashMap();
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        //{success:boolean,msg:string,file_path:string}
        if (u == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录");
            return resultMap;
        }
        if (iUserService.checkAdminRole(u).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);

            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }

            String url = PropertiesUtil.getPropery("ftp.server.http.prefix") + targetFileName;

            Map map = Maps.newHashMap();
            map.put("uri", targetFileName);
            map.put("url", url);

            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.setHeader("Access-Control-Allow-Headers","X-File-Name ");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "需要管理员权限");
            return resultMap;
        }
    }
}
