package com.joe.product.service.impl;

import com.google.common.collect.Lists;
import com.joe.category.client.CategoryClient;
import com.joe.product.config.ProductProperties;
import com.joe.product.domain.Product;
import com.joe.product.repository.ProductRepository;
import com.joe.product.repository.predicate.ProductPredicate;
import com.joe.product.service.ProductService;
import com.joe.product.vo.ProductDetailVO;
import com.joe.product.vo.ProductListVO;
import com.joe.user.common.Const;
import com.joe.user.common.ServerResponse;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductProperties productProperties;
    @Autowired
    private CategoryClient categoryClient;

    @Override
    public ServerResponse<String> saveOrUpdateProduct(Product product) {
        if (product != null) {
            //头图和子图列表第一项保持一致
            String[] images = product.getSubImages().split(",");
            product.setMainImage(images[0]);

            String operation = product.getId() != null ? "更新" : "新建";

            try {
                productRepository.save(product);
                log.info("产品"+operation+"成功");
            } catch (Exception e) {
                log.error(e.getMessage());
                log.info("产品" + operation + "失败");
            }
        }
        return ServerResponse.createByErrorMessage("产品信息不能为空");
    }

    @Override
    public ServerResponse<String> updateSaleStatus(Integer productId, Integer status) {
        //不进行数据校验,将数据校验留给controller
        Integer integer = productRepository.updateStatusById(productId, status);
        if (integer>0){
            return ServerResponse.createBySuccess("产品更新成功");
        }
        return ServerResponse.createByErrorMessage("产品更新失败");
    }

    @Override
    public ServerResponse<ProductDetailVO> manageProductDetail(Integer productId) {
        Product product = productRepository.getOne(productId);

        if (product==null){
            return ServerResponse.createByErrorMessage("产品不存在或已经下架");
        }

        ProductDetailVO productDetailVO = assembleProductDeatailVO(product);

        return ServerResponse.createBySuccess(productDetailVO);
    }

    @Override
    public ServerResponse<Page<Product>> getProductList(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        if (productPage.getContent().size()>0){
            return ServerResponse.createBySuccess(productPage);
        }
        return ServerResponse.createByErrorMessage("请传入正确的分页查询参数");
    }

    @Override
    public ServerResponse<Page<ProductListVO>> searchProduct(String productName, Pageable pageable) {
        Page<Product> productPage = productRepository.findAllByNameLike("%" + productName + "%", pageable);

        if (!(productPage.getContent().size()>0)){
            return ServerResponse.createByErrorMessage("分页查询失败,返回结果为空,请检查传入的参数");
        }

        List<Product> productList = productPage.getContent();

        List<ProductListVO> productListVOList = Lists.newArrayList();

        for (Product product:
             productList) {
            productListVOList.add(assembleProdutListVO(product));
        }

        Page<ProductListVO> productListVOPage = new PageImpl<>(
                productListVOList, pageable, productPage.getTotalElements()
        );

        return ServerResponse.createBySuccess(productListVOPage);
    }

    @Override
    public ServerResponse<ProductDetailVO> getProductDetail(Integer productId) {
        Product product = productRepository.getOne(productId);

        if(product==null||product.getStatus()!= Const.PRODUCT_STATUS.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或已删除");
        }

        ProductDetailVO productDetailVO = assembleProductDeatailVO(product);
        return ServerResponse.createBySuccess(productDetailVO);

    }

    @Override
    public ServerResponse<Page<ProductListVO>> getProductBykeywordCategory(String keyword, Integer categoryId
            , PageRequest pageRequest) {
        //获取当前种类id以及子种类id列表
        //TODO use categoryClient
        ServerResponse<List<Integer>> deepCategoryAndChildren = categoryClient.getCategoryIdList(categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();

        if (deepCategoryAndChildren.isSuccess()){
            categoryIdList = deepCategoryAndChildren.getData();
        }else {
            return ServerResponse.createByErrorMessage("获取商品列表出现异常");
        }

        List<Product> productList = Lists.newArrayList();
        //根据keyword(产品名称)和种类id列表动态查询商品
        Predicate predicate = ProductPredicate.createPredicate(keyword, categoryIdList);
        Page<Product> productPage = productRepository.findAll(predicate, pageRequest);
        productPage.getContent().forEach(product -> productList.add(product));

        List<ProductListVO> productListVOList = Lists.newArrayList();

        for (Product p:
             productList) {
            ProductListVO p_list_vo = new ProductListVO();
            BeanUtils.copyProperties(p, p_list_vo);
            p_list_vo.setImageHost(productProperties.getImageHost());
        }

        Page<ProductListVO> productListVOPage = new PageImpl<>(
                productListVOList, pageRequest, productPage.getTotalElements()
        );

        return ServerResponse.createBySuccess(productListVOPage);
    }

    private ProductDetailVO assembleProductDeatailVO(Product product) {
        ProductDetailVO productDetailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product, productDetailVO);

        //设置ftp服务器host
        productDetailVO.setImageHost(productProperties.getImageHost());
        productDetailVO.setUpdateTime(new Date());
        //设置父类id
//        Integer parentCategoryId = product.getCategory().getParentCategory().getId();
//        if (parentCategoryId != null) {
//            productDetailVO.setParentCategoryId(parentCategoryId);
//        } else {
//            productDetailVO.setParentCategoryId(0);
//        }

        return productDetailVO;
    }


    private ProductListVO assembleProdutListVO(Product product){
        ProductListVO productListVO = new ProductListVO();

        BeanUtils.copyProperties(product, productListVO);

        //productListVO.setCategoryId(product.getCategory().getId());

        return productListVO;
    }
}
