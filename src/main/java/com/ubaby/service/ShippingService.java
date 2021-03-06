package com.ubaby.service;

import com.github.pagehelper.PageInfo;
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

    /**
     * 更新地址
     *
     * @param userId
     * @param shipping
     * @return
     */
    ServerResponse<String> update(Integer userId, Shipping shipping);

    /**
     * 查询收获地址
     *
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    /**
     * 获取地址列表
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo<Shipping>> list(Integer userId, int pageNum, int pageSize);
}
