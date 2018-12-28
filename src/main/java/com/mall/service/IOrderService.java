package com.mall.service;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.vo.OrderVO;

import java.util.Map;

public interface IOrderService {
    //alipay
    ServerResponse pay(Integer userId,long orderNo,String path);

    ServerResponse<OrderVO> createOrder(Integer userId, Integer shippingId);

    ServerResponse<String> cancelOrder(Integer userId, long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse getDetail(Integer userId, long orderNo);

    ServerResponse getOrderList(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse manageGetOrderList(Integer pageNum, Integer pageSize);

    ServerResponse<OrderVO> manageDetail(long orderNo);

    ServerResponse<PageInfo> manageSearch(Integer pageNum, Integer pageSize, long orderNo);

    ServerResponse<PageInfo> manageSendGoods(long orderNo);

    ServerResponse aliCallback(Map<String,String> params);

    ServerResponse queryOrderPayStatus(Integer userId, long orderNo);

     void closeOrder(Integer hour);
}
