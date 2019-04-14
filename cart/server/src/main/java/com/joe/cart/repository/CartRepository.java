package com.joe.cart.repository;

import com.joe.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByUserIdAndProductId(Integer userId, Integer productId);

    @Modifying
    void deleteByUserIdAndProductIdIn(Integer userId, List<Integer> productIdList);

    @Query("SELECT c FROM Cart c WHERE c.userId=?1 AND c.checked=1")
    List<Cart> getCheckedCartBuUserId(Integer userId);

    @Modifying
    @Query("UPDATE Cart c SET c.checked=?3 WHERE c.userId=?1 AND c.productId=?2")
    void updateChecked(Integer userId, Integer productId, Integer checked);

    Integer countByUserId(Integer userId);
}
