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

    private List<OrderItemVO> orderItemVoList;

    private BigDecimal productTotalPrice;

    private String imageHost;

    public List<OrderItemVO> getorderItemVoList() {
        return orderItemVoList;
    }

    public void setorderItemVoList(List<OrderItemVO> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
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
