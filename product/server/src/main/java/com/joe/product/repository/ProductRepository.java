package com.joe.product.repository;

import com.joe.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>,
        QuerydslPredicateExecutor<Product> {
    List<Product> findByNameAndId(String productName, int productId);

    @Query("SELECT p.stock from Product p WHERE p.id=?1")
    Integer getStockByProdutId(Integer productId);


    @Query("UPDATE Product p set p.status=?2 WHERE p.id=?1")
    @Modifying
    Integer updateStatusById(Integer id, Integer status);

    Page<Product> findAllByNameLike(String name, Pageable pageable);
}
