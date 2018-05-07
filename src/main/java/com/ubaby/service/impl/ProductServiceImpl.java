package com.ubaby.service.impl;

import com.ubaby.common.ResponseCode;
import com.ubaby.common.ServerResponse;
import com.ubaby.dao.CategoryMapper;
import com.ubaby.dao.ProductMapper;
import com.ubaby.pojo.Category;
import com.ubaby.pojo.Product;
import com.ubaby.service.ProductService;
import com.ubaby.util.DateTimeUtil;
import com.ubaby.util.PropertiesUtil;
import com.ubaby.vo.ProductDetail;
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

    @Autowired
    private CategoryMapper categoryMapper;

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

            int rowCount;
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

    /**
     * 商品详细信息管理
     *
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetail> manageProductDetail(Integer productId) {

        if (productId == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return ServerResponse.createByErrorMessage("产品已下架或者删除");

        ProductDetail productDetail = assembleProductDetail(product);
        return ServerResponse.createBySuccess(productDetail);

    }

    /*==================================private methods==================================*/

    private ProductDetail assembleProductDetail(Product product) {

        ProductDetail productDetail = new ProductDetail();
        productDetail.setCategoryId(product.getId());
        productDetail.setCategoryId(product.getCategoryId());
        productDetail.setName(product.getName());
        productDetail.setSubTitle(product.getSubtitle());
        productDetail.setSubImage(product.getSubImages());
        productDetail.setMainImage(product.getMainImage());
        productDetail.setDetail(product.getDetail());
        productDetail.setStock(product.getStock());
        productDetail.setStatus(product.getStatus());
        productDetail.setPrice(product.getPrice());

        productDetail.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null)
            productDetail.setParentCategoryId(0);
        else
            productDetail.setParentCategoryId(category.getParentId());

        productDetail.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetail.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetail;

    }

}
