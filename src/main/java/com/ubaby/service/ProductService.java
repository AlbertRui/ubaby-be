package com.ubaby.service;

import com.github.pagehelper.PageInfo;
import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.Product;
import com.ubaby.vo.ProductDetail;
import com.ubaby.vo.ProductList;

/**
 * @author AlbertRui
 * @date 2018-05-06 21:16
 */
@SuppressWarnings("JavaDoc")
public interface ProductService {

    /**
     * 更新或新增产品
     *
     * @param product
     * @return
     */
    ServerResponse<String> saveOrUpdateProduct(Product product);

    /**
     * 产品上下架
     *
     * @param productId
     * @param status
     * @return
     */
    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    /**
     * 商品详细信息管理
     *
     * @param productId
     * @return
     */
    ServerResponse<ProductDetail> manageProductDetail(Integer productId);

    /**
     * 获取商品列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo<ProductList>> getProductList(int pageNum, int pageSize);

}
