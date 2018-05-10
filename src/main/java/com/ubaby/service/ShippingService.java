package com.ubaby.service;

import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.Shipping;

import java.util.Map;

/**
 * @author AlbertRui
 * @date 2018-05-10 22:17
 */
@SuppressWarnings("JavaDoc")
public interface ShippingService {

    /**
     * 添加收货地址
     *
     * @param userId
     * @param shipping
     * @return
     */
    ServerResponse<Map<String, Integer>> add(Integer userId, Shipping shipping);

    /**
     * 删除地址
     *
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse<String> delete(Integer userId, Integer shippingId);

}
