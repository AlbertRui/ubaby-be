package com.ubaby.controller.portal;

import com.github.pagehelper.PageInfo;
import com.ubaby.common.ServerResponse;
import com.ubaby.service.ProductService;
import com.ubaby.vo.ProductDetail;
import com.ubaby.vo.ProductList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author AlbertRui
 * @date 2018-05-08 15:23
 */
@RequestMapping("/product/")
@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetail> detail(Integer productId) {
        return productService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo<ProductList>> list(@RequestParam(value = "keyWord", required = false) String keyWord,
                                                      @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                      @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return productService.getProductByKeyWordCategory(keyWord, categoryId, pageNum, pageSize, orderBy);
    }

}

