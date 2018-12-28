package com.mall.dao;

import com.mall.pojo.Product;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> selectByNameAndId(@Param("productName") String productName, @Param("productId") int productId);

    List<Product> selectByNameAndCategoryIds(@Param("productName") String productName,
                                             @Param("categoryIdList") List<Integer> categoryIdList);

    Integer getStockByProductId(Integer productId);
}