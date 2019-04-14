package com.joe.cart.repository;

import com.google.common.collect.Lists;
import com.joe.cart.CartApplicationTests;
import com.joe.cart.domain.Cart;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class CartRepositoryTest extends CartApplicationTests {
    @Autowired
    private CartRepository cartRepository;

    @Test
    public void test(){
        Cart cart = cartRepository.getOne(126);
        assertTrue(cart!=null);
    }

    @Test
    public void testFindByUserIdAndProductId(){
        Cart cart = cartRepository.findByUserIdAndProductId(21, 26);
        assertTrue(cart!=null);
    }

    @Test
    @Rollback(value = false)
    public void testDeleteByUserIdAndProductIdIn(){
        cartRepository.deleteByUserIdAndProductIdIn(21, Lists.newArrayList(23));
    }

    @Test
    public void testGetCheckedCartBuUserId(){
        List<Cart> cart = cartRepository.getCheckedCartBuUserId(21);
        assertTrue(cart.size()>0);
    }

    @Test
    @Rollback(value = false)
    public void testUpdateChecked(){
        cartRepository.updateChecked(21, 23, 1);
    }

    @Test
    public void testCountByUserId(){
        Integer count = cartRepository.countByUserId(21);
        assertTrue(count!=null);
    }
}