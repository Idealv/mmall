package com.mall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.dao.*;
import com.mall.pojo.*;
import com.mall.service.IOrderService;
import com.mall.util.BigDecimalUtil;
import com.mall.util.DateTimeUtil;
import com.mall.util.FTPUtil;
import com.mall.util.PropertiesUtil;
import com.mall.vo.OrderItemVO;
import com.mall.vo.OrderProductVO;
import com.mall.vo.OrderVO;
import com.mall.vo.ShippingVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
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
    @Autowired
    private PayInfoMapper payInfoMapper;

    @Override
    public ServerResponse pay(Integer userId, long orderNo, String path) {
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByOrderNoUserId(orderNo, userId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该订单不存在");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
        String subject = "idealmall扫码支付,订单号:" + outTradeNo;

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
        //String authCode = "用户自己的支付宝付款码"; // 条码示例，286648048691290423
        // (可选，根据需要决定是否使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
        // 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
        //        String discountableAmount = "1.00"; //

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共")
                .append(totalAmount)
                .append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);

        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = "5m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
        for (OrderItem orderItem :
                orderItemList) {
            //价格单位为分
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getOrderNo().toString(),
                    orderItem.getProductName(),
                    BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(),
                            new BigDecimal(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        //String appAuthToken = "应用授权令牌";//根据真实值填写

        // 创建条码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://www.idealmall.com/order/alipay_callback.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        Configs.init("zfbinfo.properties");
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFilename = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(path, qrFilename);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    log.info("上传到ftp服务器失败!",e);
                }
                log.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getPropery("ftp.server.http.prefix") + qrFilename;
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");
            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");
            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

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

    private OrderVO assembleOrderVO(Order order, List<OrderItem> orderItemList) {
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
        if (shipping != null) {
            orderVO.setReceiverName(shipping.getReceiverName());
            ShippingVO shippingVO = assembleShippingVO(shipping);
        }

        orderVO.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVO.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVO.setEndTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVO.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVO.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));

        orderVO.setImageHost(PropertiesUtil.getPropery("ftp.server.http.prefix"));

        List<OrderItemVO> orderItemVOList = Lists.newArrayList();

        for (OrderItem orderItem :
                orderItemList) {
            orderItemVOList.add(assembleOrderItemVO(orderItem));
        }

        orderVO.setOrderItemVoList(orderItemVOList);

        return orderVO;
    }

    private OrderItemVO assembleOrderItemVO(OrderItem orderItem) {
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

    private ShippingVO assembleShippingVO(Shipping shipping) {
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
        if (order == null) {
            return ServerResponse.createByErrorMessage("此用户该订单不存在");
        }
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("已提交订单,无法退款");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int resultCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId) {
        OrderProductVO orderProductVO = new OrderProductVO();
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        BigDecimal payment = new BigDecimal("0");

        for (OrderItem orderItem :
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
        if (order != null) {
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
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVO orderVO = assembleOrderVO(order, orderItemList);
            return ServerResponse.createBySuccess(orderVO);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Override
    public ServerResponse<PageInfo> manageSearch(Integer pageNum, Integer pageSize, long orderNo) {
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
        if (order != null) {
            if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccessMessage("发货成功");
            }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    private List<OrderVO> assembleOrderVOList(Integer userId, List<Order> orderList) {
        List<OrderVO> orderVOList = Lists.newArrayList();

        for (Order orderItem :
                orderList) {
            List<OrderItem> orderItemList = Lists.newArrayList();
            if (userId == null) {
                orderItemList = orderItemMapper.getByOrderNo(orderItem.getOrderNo());
            } else {
                orderItemList = orderItemMapper.getByOrderNoUserId(orderItem.getOrderNo(), userId);
            }
            OrderVO orderVO = assembleOrderVO(orderItem, orderItemList);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    public ServerResponse aliCallback(Map<String,String> params){
        Long orderNo = Long.valueOf(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_no");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("此订单不是本商城订单");
        }
        //防止重复调用
        if (order.getStatus()>Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        }
        if (Const.AlipayCallback.TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatform.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderPayStatus(Integer userId,long orderNo){
        Order order = orderMapper.selectByOrderNoUserId(orderNo, userId);
        if (order==null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        //在支付宝异步回调时,改变支付状态
        if (order.getStatus()>Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
