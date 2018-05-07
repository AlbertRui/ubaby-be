package com.ubaby.controller.backend;

import com.ubaby.common.Const;
import com.ubaby.common.ResponseCode;
import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.Product;
import com.ubaby.pojo.User;
import com.ubaby.service.ProductService;
import com.ubaby.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author AlbertRui
 * @date 2018-05-06 21:06
 */
@RequestMapping("/manage/product/")
@Controller
public class ProductManageController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请您以管理员身份登录");

        if (userService.checkAdminRole(user).isSuccess())
            return productService.saveOrUpdateProduct(product);

        return ServerResponse.createByErrorMessage("无权限操作");

    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请您以管理员身份登录");

        if (userService.checkAdminRole(user).isSuccess())
            return productService.setSaleStatus(productId, status);

        return ServerResponse.createByErrorMessage("无权限操作");

    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请您以管理员身份登录");

        if (userService.checkAdminRole(user).isSuccess())
            return productService.manageProductDetail(productId);

        return ServerResponse.createByErrorMessage("无权限操作");

    }

}
