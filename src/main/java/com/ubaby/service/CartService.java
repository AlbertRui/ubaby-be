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

}
