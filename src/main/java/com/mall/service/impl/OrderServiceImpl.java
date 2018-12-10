package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.dao.*;
import com.mall.pojo.*;
import com.mall.service.IOrderService;
import com.mall.util.BigDecimalUtil;
import com.mall.util.DateTimeUtil;
import com.mall.util.PropertiesUtil;
import com.mall.vo.OrderItemVO;
import com.mall.vo.OrderProductVO;
import com.mall.vo.OrderVO;
import com.mall.vo.ShippingVO;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<OrderVO> createOrder(Integer userId, Integer shippingId) {
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        //计算总价
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        BigDecimal payment = getOrderPayment(orderItemList);
        Order order = assembleOrder(userId, shippingId, payment);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单生成失败");
        }
        for (OrderItem orderItem :
                orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }

        orderItemMapper.batchInsert(orderItemList);
        reduceProductStock(orderItemList);
        cleanCart(cartList);

        OrderVO orderVO = assembleOrderVO(order, orderItemList);
        return ServerResponse.createBySuccess(orderVO);
    }

    private OrderVO assembleOrderVO(Order order,List<OrderItem> orderItemList) {
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        orderVO.setPaymentTypeDesc(Const.PaymentType.getDescByCode(order.getPaymentType()));

        orderVO.setPostage(order.getPostage());
        orderVO.setStatus(order.getStatus());
        orderVO.setStatusDesc(Const.OrderStatusEnum.getDescByCode(order.getStatus()));
        orderVO.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping!=null){
            orderVO.setReceiverName(shipping.getReceiverName());
            ShippingVO shippingVO = assembleShippingVO(shipping);
        }

        orderVO.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVO.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVO.setEndTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVO.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVO.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));

        orderVO.setImageHost(PropertiesUtil.getPropery("ftp.server.http.prefix"));

        List<OrderItemVO> orderItemVOList=Lists.newArrayList();

        for (OrderItem orderItem:
             orderItemList) {
            orderItemVOList.add(assembleOrderItemVO(orderItem));
        }

        orderVO.setOrderItemVoList(orderItemVOList);

        return orderVO;
    }

    private OrderItemVO assembleOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setOrderNo(orderItem.getOrderNo());
        orderItemVO.setProductId(orderItem.getProductId());
        orderItemVO.setProductName(orderItem.getProductName());
        orderItemVO.setProductImage(orderItem.getProductImage());
        orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVO.setQuantity(orderItem.getQuantity());
        orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        orderItemVO.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVO;
    }

    private ShippingVO assembleShippingVO(Shipping shipping){
        ShippingVO shippingVO = new ShippingVO();
        shippingVO.setReceiverName(shipping.getReceiverName());
        shippingVO.setReceiverPhone(shipping.getReceiverPhone());
        shippingVO.setReceiverMobile(shipping.getReceiverMobile());
        shippingVO.setReceiverProvince(shipping.getReceiverProvince());
        shippingVO.setReceiverCity(shipping.getReceiverCity());
        shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVO.setReceiverAddress(shipping.getReceiverAddress());
        shippingVO.setReceiverZip(shipping.getReceiverZip());
        return shippingVO;
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem :
                orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private void cleanCart(List<Cart> cartList) {
        for (Cart cartItem :
                cartList) {
            cartMapper.deleteByPrimaryKey(cartItem.getId());
        }
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        long orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPostage(0);
        order.setPayment(payment);
        order.setPaymentType(Const.PaymentType.ONLINE_PAY.getCode());
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());

        int rowCount = orderMapper.insert(order);
        if (rowCount > 0) {
            return order;
        }
        return null;
    }

    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }


    private BigDecimal getOrderPayment(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem :
                orderItemList) {
            payment = BigDecimalUtil.add(orderItem.getTotalPrice().doubleValue(), payment.doubleValue());
        }
        return payment;
    }

    public ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for (Cart cartItem :
                cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());

            if (Const.PRODUCT_STATUS.ON_SALE.getCode() != product.getStatus()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "售完或者已下架");
            }
            //校验库存
            if (cartItem.getQuantity() > product.getStatus()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.multiply(cartItem.getQuantity().doubleValue(),
                    product.getStock().doubleValue()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    @Override
    public ServerResponse<String> cancelOrder(Integer userId, long orderNo) {
        Order order = orderMapper.selectByOrderNoUserId(orderNo, userId);
        if (order==null){
            return ServerResponse.createByErrorMessage("此用户该订单不存在");
        }
        if (order.getStatus()!=Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("已提交订单,无法退款");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int resultCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (resultCount>0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId) {
        OrderProductVO orderProductVO = new OrderProductVO();
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        BigDecimal payment = new BigDecimal("0");

        for (OrderItem orderItem:
             orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVOList.add(assembleOrderItemVO(orderItem));
        }
        orderProductVO.setImageHost(PropertiesUtil.getPropery("ftp.server.http.prefix"));
        orderProductVO.setProdcutTotalPrice(payment);
        orderProductVO.setOrderItemVOList(orderItemVOList);
        return ServerResponse.createBySuccess(orderProductVO);
    }

    @Override
    public ServerResponse getDetail(Integer userId, long orderNo) {
        Order order = orderMapper.selectByOrderNoUserId(orderNo, userId);
        if (order!=null){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
            OrderVO orderVO = assembleOrderVO(order, orderItemList);
            return ServerResponse.createBySuccess(orderVO);
        }
        return ServerResponse.createByErrorMessage("未找到此订单");
    }

    @Override
    public ServerResponse<PageInfo> getOrderList(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVO> orderVOList = assembleOrderVOList(userId, orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse manageGetOrderList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        List<OrderVO> orderVOList = assembleOrderVOList(null, orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<OrderVO> manageDetail(long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVO orderVO = assembleOrderVO(order, orderItemList);
            return ServerResponse.createBySuccess(orderVO);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Override
    public ServerResponse<PageInfo> manageSearch(Integer pageNum,Integer pageSize,long orderNo) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVO orderVO = assembleOrderVO(order, orderItemList);
            PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
            pageInfo.setList(Lists.newArrayList(orderVO));
            return ServerResponse.createBySuccess(pageInfo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Override
    public ServerResponse<PageInfo> manageSendGoods(long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            if (order.getStatus()==Const.OrderStatusEnum.PAID.getCode()){
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccessMessage("发货成功");
            }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    private List<OrderVO> assembleOrderVOList(Integer userId,List<Order> orderList){
        List<OrderVO> orderVOList=Lists.newArrayList();

        for (Order orderItem:
             orderList) {
            List<OrderItem> orderItemList=Lists.newArrayList();
            if (userId==null){
                orderItemList=orderItemMapper.getByOrderNo(orderItem.getOrderNo());
            }else {
                orderItemList = orderItemMapper.getByOrderNoUserId(orderItem.getOrderNo(), userId);
            }
            OrderVO orderVO = assembleOrderVO(orderItem, orderItemList);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

}
