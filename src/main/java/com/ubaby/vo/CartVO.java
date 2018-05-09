package com.ubaby.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车前端的value object
 *
 * @author AlbertRui
 * @date 2018-05-09 18:27
 */
public class CartVO {

    private List<CartProduct> cartProducts;
    private BigDecimal cartTotalPrice;
    //是否都勾选
    private boolean isAllChecked;
    private String imageHost;

    public List<CartProduct> getCartProducts() {
        return cartProducts;
    }

    public void setCartProducts(List<CartProduct> cartProducts) {
        this.cartProducts = cartProducts;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public boolean isAllChecked() {
        return isAllChecked;
    }

    public void setAllChecked(boolean allChecked) {
        isAllChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
