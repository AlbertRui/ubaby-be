package com.ubaby.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Order与Product结合的value object
 *
 * @author AlbertRui
 * @date 2018-05-14 16:36
 */
public class OrderProduct {

    private List<OrderItemVO> orderItemVOS;

    private BigDecimal productTotalPrice;

    private String imageHost;

    public List<OrderItemVO> getOrderItemVOS() {
        return orderItemVOS;
    }

    public void setOrderItemVOS(List<OrderItemVO> orderItemVOS) {
        this.orderItemVOS = orderItemVOS;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

}
