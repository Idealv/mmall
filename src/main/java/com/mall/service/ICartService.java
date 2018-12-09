package com.mall.service;

import com.mall.common.ServerResponse;
import com.mall.vo.CartVO;

public interface ICartService {
    ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVO> delete(Integer userId, String productIds);
    ServerResponse<CartVO> list(Integer userId);
    ServerResponse<CartVO> selectOrUnselect(Integer userId,Integer productId,Integer checked);
    ServerResponse<Integer> getCartProductCount(Integer userId);
}
