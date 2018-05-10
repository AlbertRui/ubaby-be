package com.ubaby.service.impl;

import com.google.common.collect.Maps;
import com.ubaby.common.ServerResponse;
import com.ubaby.dao.ShippingMapper;
import com.ubaby.pojo.Shipping;
import com.ubaby.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author AlbertRui
 * @date 2018-05-10 22:18
 */
@SuppressWarnings("JavaDoc")
@Transactional
@Service("shippingService")
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 添加收货地址
     *
     * @param userId
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse<Map<String, Integer>> add(Integer userId, Shipping shipping) {

        shipping.setId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0) {
            Map<String, Integer> result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("新增地址成功", result);
        }

        return ServerResponse.createByErrorMessage("新增地址失败");

    }

    /**
     * 删除地址
     *
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse<String> delete(Integer userId, Integer shippingId) {

        int resultCount = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        if (resultCount > 0)
            return ServerResponse.createBySuccess("删除地址成功");

        return ServerResponse.createByErrorMessage("删除地址失败");

    }

    /**
     * 更新地址
     *
     * @param userId
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse<String> update(Integer userId, Shipping shipping) {

        shipping.setId(userId);
        int resultCount = shippingMapper.updateByShipping(shipping);
        if (resultCount > 0)
            return ServerResponse.createBySuccess("更新地址成功");

        return ServerResponse.createByErrorMessage("更新地址失败");

    }

    /**
     * 查询收获地址
     *
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {

        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId, shippingId);
        if (shipping == null)
            return ServerResponse.createByErrorMessage("无法查询到该地址");

        return ServerResponse.createBySuccess("查询址成功", shipping);

    }

}
