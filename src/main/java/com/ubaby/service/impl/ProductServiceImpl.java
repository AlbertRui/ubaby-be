package com.ubaby.service.impl;

import com.ubaby.common.ResponseCode;
import com.ubaby.common.ServerResponse;
import com.ubaby.dao.ProductMapper;
import com.ubaby.pojo.Product;
import com.ubaby.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author AlbertRui
 * @date 2018-05-06 21:17
 */
@SuppressWarnings("JavaDoc")
@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 更新或新增产品
     *
     * @param product
     * @return
     */
    @Override
    public ServerResponse<String> saveOrUpdateProduct(Product product) {

        if (product != null && StringUtils.isNotBlank(product.getSubImages())) {
            String[] subImages = product.getSubImages().split(",");
            if (subImages.length > 0)
                product.setMainImage(subImages[0]);

            int rowCount = 0;
            if (product.getId() != null) {
                rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0)
                    ServerResponse.createBySuccess("更新产品成功");

                return ServerResponse.createByErrorMessage("更新产品失败");
            }

            rowCount = productMapper.insert(product);
            if (rowCount > 0)
                ServerResponse.createBySuccess("新增产品成功");

            return ServerResponse.createByErrorMessage("新增产品失败");

        }

        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");

    }

    /**
     * 产品上下架
     *
     * @param productId
     * @param status
     * @return
     */
    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {

        if (productId == null || status == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0)
            return ServerResponse.createBySuccess("更新产品销售状态成功");

        return ServerResponse.createByErrorMessage("更新产品销售状态失败");

    }

}
