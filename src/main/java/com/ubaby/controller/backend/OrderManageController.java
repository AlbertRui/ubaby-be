package com.ubaby.controller.backend;

import com.github.pagehelper.PageInfo;
import com.ubaby.common.Const;
import com.ubaby.common.ResponseCode;
import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.User;
import com.ubaby.service.OrderService;
import com.ubaby.service.UserService;
import com.ubaby.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author AlbertRui
 * @date 2018-05-14 18:44
 */
@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo<OrderVO>> orderList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");

        if (userService.checkAdminRole(user).isSuccess())
            return orderService.manageList(pageNum, pageSize);

        return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");

    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVO> orderDetail(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");

        if (userService.checkAdminRole(user).isSuccess())
            return orderService.manageDetail(orderNo);

        return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");

    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo<OrderVO>> orderSearch(HttpSession session, Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");

        if (userService.checkAdminRole(user).isSuccess())
            return orderService.manageSearch(orderNo, pageNum, pageSize);

        return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");

    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");

        if (userService.checkAdminRole(user).isSuccess())
            return orderService.manageSendGoods(orderNo);

        return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");

    }

}
