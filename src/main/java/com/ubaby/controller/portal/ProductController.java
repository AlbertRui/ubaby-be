package com.ubaby.controller.portal;

import com.ubaby.common.ServerResponse;
import com.ubaby.service.ProductService;
import com.ubaby.vo.ProductDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
