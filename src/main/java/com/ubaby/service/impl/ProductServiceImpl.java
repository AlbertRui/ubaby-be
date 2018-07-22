package com.ubaby.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.ubaby.common.Const;
import com.ubaby.common.ResponseCode;
import com.ubaby.common.ServerResponse;
import com.ubaby.dao.CategoryMapper;
import com.ubaby.dao.ProductMapper;
import com.ubaby.pojo.Category;
import com.ubaby.pojo.Product;
import com.ubaby.service.CategoryService;
import com.ubaby.service.ProductService;
import com.ubaby.util.DateTimeUtil;
import com.ubaby.util.PropertiesUtil;
import com.ubaby.vo.ProductDetail;
import com.ubaby.vo.ProductList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author AlbertRui
 * @date 2018-05-06 21:17
 */
@SuppressWarnings("JavaDoc")
@Transactional
@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    CategoryService categoryService;

    /**
     * 更新或新增产品
     *
     * @param product
     * @return
     */
    @Override
    public ServerResponse<String> saveOrUpdateProduct(Product product) {

        if (product != null && StringUtils.isNotBlank(product.getsubImage())) {
            String[] subImage = product.getsubImage().split(",");
            if (subImage.length > 0)
                product.setMainImage(subImage[0]);

            int rowCount;
            if (product.getId() != null) {
                rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0)
                    return ServerResponse.createBySuccess("更新产品成功");

                return ServerResponse.createByErrorMessage("更新产品失败");
            }

            rowCount = productMapper.insert(product);
            if (rowCount > 0)
                return ServerResponse.createBySuccess("新增产品成功");

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

    /**
     * 获取商品列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo<ProductList>> getProductList(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        List<Product> products = productMapper.selectList();
        List<ProductList> productLists = Lists.newArrayList();
        for (Product product : products)
            productLists.add(assembleProductList(product));

        PageInfo<ProductList> pageInfo = new PageInfo(products);
        pageInfo.setList(productLists);

        return ServerResponse.createBySuccess(pageInfo);

    }

    /**
     * 后台商品搜索
     *
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo<ProductList>> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName))
            productName = "%" + productName + "%";

        List<Product> products = productMapper.selectByNameAndId(productName, productId);
        List<ProductList> productLists = Lists.newArrayList();
        for (Product product : products)
            productLists.add(assembleProductList(product));

        PageInfo<ProductList> pageInfo = new PageInfo(products);
        pageInfo.setList(productLists);

        return ServerResponse.createBySuccess(pageInfo);

    }

    /**
     * 前台获取商品详细信息
     *
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetail> getProductDetail(Integer productId) {

        if (productId == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null || product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode())
            return ServerResponse.createByErrorMessage("产品已下架或者删除");

        ProductDetail productDetail = assembleProductDetail(product);
        return ServerResponse.createBySuccess(productDetail);

    }

    /**
     * 前台根据关键字和商品分类获取商品
     *
     * @param keyWord
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo<ProductList>> getProductByKeyWordCategory(String keyWord, Integer categoryId, int pageNum, int pageSize, String orderBy) {

        if (StringUtils.isBlank(keyWord) && categoryId == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        List<Integer> categoryIds = Lists.newArrayList();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            //如果分类为空，并且关键字为空，就返回一个空的结果集
            if (category == null && StringUtils.isBlank(keyWord)) {
                PageHelper.startPage(pageNum, pageSize);
                List<ProductList> productLists = Lists.newArrayList();
                PageInfo<ProductList> pageInfo = new PageInfo<>(productLists);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIds = categoryService.getCategoryAndChildrenById(category.getId()).getData();
        }

        if (StringUtils.isNotBlank(keyWord))
            keyWord = "%" + keyWord + "%";

        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderBys = orderBy.split("_");
                PageHelper.orderBy(orderBys[0] + " " + orderBys[1]);
            }
        }

        List<Product> products = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyWord) ? null : keyWord, categoryIds.size() == 0 ? null : categoryIds);
        List<ProductList> productLists = Lists.newArrayList();
        for (Product product : products)
            productLists.add(assembleProductList(product));

        PageInfo<ProductList> pageInfo = new PageInfo(products);
        pageInfo.setList(productLists);

        return ServerResponse.createBySuccess(pageInfo);

    }

    /*==================================private methods==================================*/

    private ProductDetail assembleProductDetail(Product product) {

        ProductDetail productDetail = new ProductDetail();
        productDetail.setCategoryId(product.getId());
        productDetail.setCategoryId(product.getCategoryId());
        productDetail.setName(product.getName());
        productDetail.setSubTitle(product.getSubtitle());
        productDetail.setSubImage(product.getsubImage());
        productDetail.setMainImage(product.getMainImage());
        productDetail.setDetail(product.getDetail());
        productDetail.setStock(product.getStock());
        productDetail.setStatus(product.getStatus());
        productDetail.setPrice(product.getPrice());

        productDetail.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.ubaby.rzhang.xin/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null)
            productDetail.setParentCategoryId(0);
        else
            productDetail.setParentCategoryId(category.getParentId());

        productDetail.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetail.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetail;

    }

    private ProductList assembleProductList(Product product) {

        ProductList productList = new ProductList();
        productList.setId(product.getId());
        productList.setCategoryId(product.getCategoryId());
        productList.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.ubaby.rzhang.xin/"));
        productList.setMainImage(product.getMainImage());
        productList.setName(product.getName());
        productList.setPrice(product.getPrice());
        productList.setStatus(product.getStatus());
        productList.setSubTitle(product.getSubtitle());

        return productList;

    }

}
