package com.ubaby.service;

import com.ubaby.common.ServerResponse;
import com.ubaby.vo.CartVO;

/**
 * @author AlbertRui
 * @date 2018-05-09 18:02
 */
@SuppressWarnings("JavaDoc")
public interface CartService {

    /**
     * 向购物车中添加商品
     *
     * @param userId
     * @param count
     * @param productId
     * @return
     */
    ServerResponse<CartVO> add(Integer userId, Integer count, Integer productId);

    /**
     * 更新购物车
     *
     * @param userId
     * @param count
     * @param productId
     * @return
     */
    ServerResponse<CartVO> update(Integer userId, Integer count, Integer productId);

    /**
     * 删除购物车中的商品
     *
     * @param userId
     * @param productIds
     * @return
     */
    ServerResponse<CartVO> deleteProduct(Integer userId, String productIds);

    /**
     * 查询购物车
     *
     * @param userId
     * @return
     */
    ServerResponse<CartVO> list(Integer userId);

    /**
     * 全选或者全不选
     *
     * @param userId
     * @return
     */
    ServerResponse<CartVO> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    /**
     * 获取购物车商品数量
     *
     * @param userId
     * @return
     */
    ServerResponse<Integer> getCartProductCount(Integer userId);

}
