package com.mall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.dao.CartMapper;
import com.mall.dao.ProductMapper;
import com.mall.pojo.Cart;
import com.mall.pojo.Product;
import com.mall.service.ICartService;
import com.mall.util.BigDecimalUtil;
import com.mall.util.PropertiesUtil;
import com.mall.vo.CartProductVO;
import com.mall.vo.CartVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count) {
        if (productId==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        if (cart == null) {
            //购物车没有此商品
            Cart cartItem = new Cart();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setQuantity(count);
        } else {
            count += cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }


    @Override
    public ServerResponse<CartVO> update(Integer userId, Integer productId, Integer count) {
        if (productId==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        if (cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVO> delete(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (productIds.isEmpty()){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId, productList);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVO> list(Integer userId) {
        CartVO cartVO = this.getCartVOLimit(userId);
        return ServerResponse.createBySuccess(cartVO);
    }


    @Override
    public ServerResponse<CartVO> selectOrUnselect(Integer userId,Integer productId,Integer checked) {
        cartMapper.updatecheckOrUncheckProduct(userId, productId,checked);
        return this.list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId==null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    public CartVO getCartVOLimit(Integer userId) {
        CartVO cartVO = new CartVO();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartProductVO> cartProductVOList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem:
                 cartList) {
                CartProductVO cartProductVO = new CartProductVO();
                cartProductVO.setId(cartItem.getId());
                cartProductVO.setUserId(cartItem.getUserId());
                cartProductVO.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product!=null){
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStock(product.getStock());

                    int buyLimitCount=0;
                    if (product.getStock()>=cartItem.getQuantity()){
                        buyLimitCount=cartItem.getQuantity();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        buyLimitCount=product.getStock();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAILED);

                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVO.setQuantity(buyLimitCount);
                    cartProductVO.setProductTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(),
                            cartItem.getQuantity().doubleValue()));
                    cartProductVO.setProductChecked(cartItem.getChecked());
                }
                if (cartProductVO.getProductChecked()==Const.Cart.CHECKED){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),
                            cartProductVO.getProductTotalPrice().doubleValue());
                }
                cartProductVOList.add(cartProductVO);
            }
        }
        //set cartVO
        cartVO.setCartTotalPrice(cartTotalPrice);
        cartVO.setCartProductVOList(cartProductVOList);
        cartVO.setAllChecked(this.getAllCheckedStatus(userId));
        cartVO.setImageHost(PropertiesUtil.getPropery("ftp.server.http.prefix"));
        return cartVO;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if (userId==null){
            return false;
        }
        return cartMapper.selectAllCheckedStatus(userId)==0;
    }
}
