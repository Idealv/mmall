package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.dao.CategoryMapper;
import com.mall.dao.ProductMapper;
import com.mall.pojo.Category;
import com.mall.pojo.Product;
import com.mall.service.ICategoryService;
import com.mall.service.IProductService;
import com.mall.util.DateTimeUtil;
import com.mall.util.PropertiesUtil;
import com.mall.vo.ProductDetailVO;
import com.mall.vo.ProductListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;
    @Override
    public ServerResponse<String> saveOrUpdateProduct(Product product) {
        if (product!=null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] images = product.getSubImages().split(",");
                product.setMainImage(images[0]);
            }
            int resultCount;
            if (product.getId()!=null){
                //更新
                resultCount = productMapper.updateByPrimaryKey(product);
                if (resultCount>0){
                    return ServerResponse.createBySuccessMessage("产品更新失败成功");
                }
                return ServerResponse.createByErrorMessage("产品更新失败");
            }else {
                //保存
                resultCount = productMapper.insert(product);
                if (resultCount>0){
                    return ServerResponse.createBySuccessMessage("产品保存失败成功");
                }
                return ServerResponse.createByErrorMessage("产品保存失败");
            }
        }
        return ServerResponse.createByErrorMessage("产品参数不正确");
    }

    @Override
    public ServerResponse<String> updateSaleStatus(Integer productId, Integer status) {
        if (productId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if (resultCount>0){
            return ServerResponse.createBySuccessMessage("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVO> manageProductDetail(Integer productId) {
        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }
        ProductDetailVO productDetailVO = assembleProdcutDetailVO(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectList();
        List<ProductListVO> productListVO= Lists.newArrayList();
        for (Product product:
             products) {
            productListVO.add(assembleProductListVO(product));
        }

        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVO);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setImageHost(PropertiesUtil.getPropery("ftp.server.http.prefix", "http://img.happymmall.com/"));
        productListVO.setMainImage(product.getMainImage());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        return productListVO;
    }

    public ProductDetailVO assembleProdcutDetailVO(Product product){
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setId(product.getId());
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setName(product.getName());
        productDetailVO.setSubImages(product.getSubtitle());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setStatus(product.getStatus());


        productDetailVO.setImageHost(PropertiesUtil.getPropery("ftp.server.http.prefix"
                , "http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getId());
        if (category==null){
            productDetailVO.setParentCategoryId(0);
        }else {
            productDetailVO.setParentCategoryId(category.getParentId());
        }
        productDetailVO.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVO.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVO;
    }

    public ServerResponse<PageInfo> searchProduct(String productName, int productId, int pageNum, int pageSize) {
        if (StringUtils.isNotBlank(productName)){
            productName = "%" + productName + "%";
        }
        List<Product> products = productMapper.selectByNameAndId(productName, productId);
        List<ProductListVO> productListVO= Lists.newArrayList();
        for (Product product:
                products) {
            productListVO.add(assembleProductListVO(product));
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVO);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<ProductDetailVO> getProductDetail(Integer productId){
        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null||product.getStatus()!= Const.PRODUCT_STATUS.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }
        ProductDetailVO productDetailVO = assembleProdcutDetailVO(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }


    public ServerResponse<PageInfo> getProductBykeywordCategory(String keyword,
                                                                Integer categoryId,
                                                                Integer pageNum,Integer pageSize,
                                                                String orderBy){
        if (StringUtils.isBlank(keyword)&&categoryId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList=Lists.newArrayList();
        if (categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category==null&&StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum, pageSize);
                List emptyList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(emptyList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList=iCategoryService.getDeepCategoryAndChildren(category.getId()).getData();
        }

        PageHelper.startPage(pageNum, pageSize);

        if (Const.ProductListOrderby.PRICE_ASC_DESC.contains(orderBy)){
            String[] orderByArr = orderBy.split("_");
            PageHelper.orderBy(orderByArr[0] + " " + orderByArr[1]);
        }
        keyword=StringUtils.isBlank(keyword)?null:keyword;
        categoryIdList=categoryIdList.size()==0?null:categoryIdList;
        List<Product> products = productMapper.selectByNameAndCategoryIds(keyword, categoryIdList);
        List<ProductListVO> productListVOList=Lists.newArrayList();
        for (Product productItem:
             products) {
            ProductListVO productListVO = assembleProductListVO(productItem);
            productListVOList.add(productListVO);
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVOList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
