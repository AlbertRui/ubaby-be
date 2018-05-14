package com.ubaby.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.ubaby.common.Const;
import com.ubaby.common.ResponseCode;
import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.User;
import com.ubaby.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author AlbertRui
 * @date 2018-05-12 18:09
 */
@RequestMapping("/order/")
@Controller
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse<Map<String, String>> pay(HttpSession session, HttpServletRequest request, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        String path = request.getSession().getServletContext().getRealPath("upload");

        return orderService.pay(orderNo, user.getId(), path);

    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallBack(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String name : parameterMap.keySet()) {
            String[] values = parameterMap.get(name);
            StringBuilder value = new StringBuilder();
            int i;
            for (i = 0; i < values.length - 1; i++)
                value.append(values[i]).append(",");
            value.append(values[i]);
            params.put(name, value.toString());
        }

        LOGGER.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());
        //非常重要，验证毁掉的正确性，是不是支付宝发的，并且还要避免重复通知
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2)
                return ServerResponse.createByErrorMessage("非法请求，验证不通过");
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝验证回调异常", e);
        }

        // todo 验证各种数据

        ServerResponse<String> serverResponse = orderService.alipayCallBack(params);
        if (serverResponse.isSuccess())
            return Const.AlipayCallback.RESPONSE_SUCCESS;

        return Const.AlipayCallback.RESPONSE_FAILED;

    }

    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        ServerResponse<Boolean> response = orderService.queryOrderPayStatus(user.getId(), orderNo);
        if (response.isSuccess())
            return ServerResponse.createBySuccess(true);

        return ServerResponse.createBySuccess(false);

    }

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return orderService.create(user.getId(), shippingId);

    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse<String> cancel(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return  orderService.cancel(user.getId(), orderNo);

    }

}
