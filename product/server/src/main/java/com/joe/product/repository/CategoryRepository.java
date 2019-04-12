package com.joe.product.repository;

import com.joe.product.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer>{
    List<Category> findAllByParentCategoryId(Integer parentId);
}
