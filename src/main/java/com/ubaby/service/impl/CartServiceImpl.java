package com.ubaby.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.ubaby.common.Const;
import com.ubaby.common.ResponseCode;
import com.ubaby.common.ServerResponse;
import com.ubaby.dao.CartMapper;
import com.ubaby.dao.ProductMapper;
import com.ubaby.pojo.Cart;
import com.ubaby.pojo.Product;
import com.ubaby.service.CartService;
import com.ubaby.util.BigDecimalUtil;
import com.ubaby.util.PropertiesUtil;
import com.ubaby.vo.CartProduct;
import com.ubaby.vo.CartVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author AlbertRui
 * @date 2018-05-09 18:04
 */
@SuppressWarnings("JavaDoc")
@Transactional
@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 向购物车中添加商品
     *
     * @param userId
     * @param count
     * @param productId
     * @return
     */
    public ServerResponse<CartVO> add(Integer userId, Integer count, Integer productId) {

        if (productId == null || count == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        if (cart == null) {
            cart = new Cart();
            cart.setChecked(Const.Cart.CHECKED);
            cart.setProductId(productId);
            cart.setQuantity(count);
            cart.setUserId(userId);
            cartMapper.insert(cart);
        } else {
            cart.setQuantity(cart.getQuantity() + count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        return list(userId);

    }

    /**
     * 更新购物车
     *
     * @param userId
     * @param count
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<CartVO> update(Integer userId, Integer count, Integer productId) {

        if (productId == null || count == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }

        cartMapper.updateByPrimaryKeySelective(cart);

        return list(userId);

    }

    /**
     * 删除购物车中的商品
     *
     * @param userId
     * @param productIds
     * @return
     */
    @Override
    public ServerResponse<CartVO> deleteProduct(Integer userId, String productIds) {

        List<String> ids = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(ids))
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        cartMapper.deleteByUserIdAndProductIds(userId, ids);

        return list(userId);

    }

    /**
     * 查询购物车
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<CartVO> list(Integer userId) {
        return ServerResponse.createBySuccess(getCartVOLimit(userId));
    }

    /**
     * 全选或者全不选
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<CartVO> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUnCheckedProduct(userId, productId, checked);
        return list(userId);
    }

    /**
     * 获取购物车商品数量
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {

        if (userId == null)
            return ServerResponse.createBySuccess(0);

        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));

    }

    /*==================================private method================================*/

    private CartVO getCartVOLimit(Integer userId) {

        CartVO cartVO = new CartVO();
        List<Cart> carts = cartMapper.selectListByUserId(userId);
        List<CartProduct> cartProducts = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(carts)) {
            CartProduct cartProduct;
            for (Cart cart : carts) {
                cartProduct = new CartProduct();
                cartProduct.setId(cart.getId());
                cartProduct.setProductId(cart.getProductId());
                cartProduct.setUserId(cart.getUserId());

                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product != null) {
                    cartProduct.setProductMainImage(product.getMainImage());
                    cartProduct.setProductSubtitle(product.getSubtitle());
                    cartProduct.setProductName(product.getName());
                    cartProduct.setProductPrice(product.getPrice());
                    cartProduct.setProductStatus(product.getStatus());
                    cartProduct.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount;
                    if (product.getStock() >= cart.getQuantity()) {
                        //库存充足的时候
                        buyLimitCount = cart.getQuantity();
                        cartProduct.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProduct.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProduct.setQuantity(buyLimitCount);

                    cartProduct.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProduct.getQuantity().doubleValue()));
                    cartProduct.setProductChecked(cart.getChecked());
                }

                //如果已经勾选，就加到整个购物车的总价格中
                if (cart.getChecked() == Const.Cart.CHECKED)
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProduct.getProductTotalPrice().doubleValue());

                cartProducts.add(cartProduct);
            }
        }
        cartVO.setCartTotalPrice(cartTotalPrice);
        cartVO.setCartProducts(cartProducts);
        cartVO.setAllChecked(getAllCheckedStatus(userId));
        cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVO;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        return userId != null && cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }

}
