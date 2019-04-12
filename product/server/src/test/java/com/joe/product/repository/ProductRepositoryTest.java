package com.joe.product.repository;

import com.google.common.collect.Lists;
import com.joe.product.ProductApplicationTests;
import com.joe.product.config.ProductProperties;
import com.joe.product.domain.Product;
import com.joe.product.repository.predicate.ProductPredicate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

@Transactional
public class ProductRepositoryTest extends ProductApplicationTests {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductProperties productProperties;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void testGet() throws Exception {
        Optional<Product> productOptional = productRepository.findById(26);
        Product product = productOptional.get();
        System.out.println(product);
    }

    @Test
    public void testFindByNameAndId() throws Exception {
        String productName = "Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机";
        List<Product> productList = productRepository.findByNameAndId(productName, 26);
    }

    @Test
    public void testGetStockByProdutId() throws Exception {
        Integer stock = productRepository.getStockByProdutId(26);
        System.out.println(stock);
    }

    @Test
    public void testUpdate() throws Exception{
        Product product = productRepository.getOne(29);

        product.setSubtitle("这是一个测试");
        productRepository.save(product);
    }

    @Test
    @Rollback(value = false)
    public void testUpdateStatusById() throws Exception{
        Integer result = productRepository.updateStatusById(26, 0);
        System.out.println(result);
    }

    @Test
    public void testProperties() throws Exception{
        String imageHost = productProperties.getImageHost();
        System.out.println(imageHost);
    }


    @Test
    public void testPage() throws Exception{
        Page<Product> productPage = productRepository.findAll(PageRequest.of(0, 2));
        List<Product> content = productPage.getContent();
    }

    @Test
    public void testFindAllByNameLike() throws Exception{
        Page<Product> productPage = productRepository.findAllByNameLike("%iPhone%", PageRequest.of(0, 2));
        System.out.println(productPage);
    }

    @Test
    public void testFindAllByNameLikeAndCategoryIn() throws Exception {
//        List<Integer> categoryIdList = Arrays.asList(100002);
//        Page<Product> productPage = productRepository.findAllByNameLikeAndCategoryIn(
//                "%iPhone%",
//                categoryIdList,
//                PageRequest.of(0, 2,Sort.by("price")));
//        System.out.println(productPage);
    }

    @Test
    public void testDSL() throws Exception {
        List<Product> productList = Lists.newArrayList();
        Iterable<Product> products = productRepository.findAll(
                ProductPredicate.createPredicate(null, Lists.newArrayList(100002)));
        products.forEach(product -> productList.add(product));

        assertTrue(productList.size()>0);
    }

}