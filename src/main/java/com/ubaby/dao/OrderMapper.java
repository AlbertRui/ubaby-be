package com.ubaby.dao;

import com.ubaby.pojo.Cart;
import com.ubaby.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    Order selectByOrderNo(Long orderNo);

    List<Order> selectOrderListByUserId(Integer userId);

    List<Order> selectAllOrder();
}