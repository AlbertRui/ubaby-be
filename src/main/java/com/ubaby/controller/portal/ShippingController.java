package com.ubaby.controller.portal;

import com.ubaby.common.Const;
import com.ubaby.common.ResponseCode;
import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.Shipping;
import com.ubaby.pojo.User;
import com.ubaby.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author AlbertRui
 * @date 2018-05-10 22:16
 */
@RequestMapping("/shipping/")
@Controller
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return shippingService.add(user.getId(), shipping);

    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session, Integer shippingId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return shippingService.delete(user.getId(), shippingId);

    }

}
