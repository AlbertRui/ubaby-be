package com.ubaby.service;

import com.github.pagehelper.PageInfo;
import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.OrderItem;
import com.ubaby.vo.OrderProduct;
import com.ubaby.vo.OrderVO;

import java.util.List;
import java.util.Map;

/**
 * @author AlbertRui
 * @date 2018-05-12 18:26
 */
@SuppressWarnings("JavaDoc")
public interface OrderService {

    /**
     * 支付操作
     *
     * @param orderNo
     * @param userId
     * @param path
     * @return
     */
    ServerResponse<Map<String, String>> pay(Long orderNo, Integer userId, String path);

    /**
     * 支付宝回调
     *
     * @param params
     * @return
     */
    ServerResponse<String> alipayCallBack(Map<String, String> params);

    /**
     * 查询订单状态
     *
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo);

    /**
     * 创建订单
     *
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse create(Integer userId, Integer shippingId);

    /**
     * 删除订单
     *
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<String> cancel(Integer userId, Long orderNo);

    /**
     * 获取购物车中已经选中的商品
     *
     * @param userId
     * @return
     */
    ServerResponse getOrderCartProduct(Integer userId);

    /**
     * 获取订单详情
     *
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<OrderVO> getOrderDetail(Integer userId, Long orderNo);

    /**
     * 个人中心查看订单
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo<OrderVO>> getOrderList(Integer userId, int pageNum, int pageSize);

    /**
     * 管理员查看订单
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo<OrderVO>> manageList(int pageNum, int pageSize);

    /**
     * 管理员查看订单详情
     *
     * @param orderNo
     * @return
     */
    ServerResponse<OrderVO> manageDetail(Long orderNo);

    /**
     * 后台管理员按订单号搜索
     *
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo<OrderVO>> manageSearch(Long orderNo, int pageNum, int pageSize);
}
