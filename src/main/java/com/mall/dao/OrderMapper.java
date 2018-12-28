package com.mall.dao;

import com.mall.pojo.Order;
import com.mall.pojo.OrderItem;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNoUserId(@Param("orderNo") long orderNo,
                                @Param("userId") Integer userId);

    List<Order> selectByUserId(Integer userId);

    Order selectByOrderNo(long orderNo);

    List<Order> selectAllOrder();

    List<Order> selectOrderByStatusCreateTime(@Param("status")Integer status,
                                              @Param("createTime")String createTime);

    int closeOrderByOrderId(Integer orderId);
}