package com.ubaby.controller.portal;

import com.ubaby.common.Const;
import com.ubaby.common.ResponseCode;
import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.User;
import com.ubaby.service.CartService;
import com.ubaby.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @author AlbertRui
 * @date 2018-05-09 17:58
 */
@RequestMapping("/cart/")
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    public ServerResponse<CartVO> add(HttpSession session, Integer count, Integer productId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return cartService.add(user.getId(), count, productId);

    }

}
