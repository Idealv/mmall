package com.mall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mall.common.ServerResponse;
import com.mall.pojo.Product;
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
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse<String> productSave(HttpServletRequest request, Product product) {
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iProductService.saveOrUpdateProduct(product);
//        }
//        return serverResponse;
        return iProductService.saveOrUpdateProduct(product);
    }


    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse<String> setSaleStatus(HttpServletRequest request, Integer productId, Integer status) {
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iProductService.updateSaleStatus(productId, status);
//        }
//        return serverResponse;
        return iProductService.updateSaleStatus(productId, status);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVO> getDetail(HttpServletRequest request, Integer productId) {
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iProductService.manageProductDetail(productId);
//        }
//        return serverResponse;
        return iProductService.manageProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpServletRequest request,
                                            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iProductService.getProductList(pageNum, pageSize);
//        }
//        return serverResponse;
        return iProductService.getProductList(pageNum, pageSize);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpServletRequest request,
                                            String productName,int productId,
                                            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
//        }
//        return serverResponse;
        return iProductService.searchProduct(productName, productId, pageNum, pageSize);
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file",required = false)MultipartFile file,
                                 HttpServletRequest request) {
//        ServerResponse serverResponse = iUserService.checkRole(request);
//        if (serverResponse.isSuccess()) {
//            String path = request.getSession().getServletContext().getRealPath("upload");
//            String targetFileName = iFileService.upload(file, path);
//            String url = PropertiesUtil.getPropery("ftp.server.http.prefix") + targetFileName;
//            Map map = Maps.newHashMap();
//            map.put("uri", targetFileName);
//            map.put("url", url);
//            return ServerResponse.createBySuccess(map);
//        }
//        return serverResponse;
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getPropery("ftp.server.http.prefix") + targetFileName;
        Map map = Maps.newHashMap();
        map.put("uri", targetFileName);
        map.put("url", url);
        return ServerResponse.createBySuccess(map);
    }

    @RequestMapping("richText_img_upload.do")
    @ResponseBody
    public Map richTextImgUpload(@RequestParam(value = "upload_file",required = false) MultipartFile file,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        Map resultMap=Maps.newHashMap();
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file,path);
        if(StringUtils.isBlank(targetFileName)){
            resultMap.put("success",false);
            resultMap.put("msg","上传失败");
            return resultMap;
        }

        String url = PropertiesUtil.getPropery("ftp.server.http.prefix")+targetFileName;
        resultMap.put("success",true);
        resultMap.put("msg","上传成功");
        resultMap.put("file_path",url);
        response.addHeader("Access-Control-Allow-Headers","X-File-Name");
        return resultMap;
    }
}
