package com.ubaby.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ubaby.common.Const;
import com.ubaby.common.ServerResponse;
import com.ubaby.dao.CartMapper;
import com.ubaby.dao.OrderItemMapper;
import com.ubaby.dao.OrderMapper;
import com.ubaby.dao.PayInfoMapper;
import com.ubaby.dao.ProductMapper;
import com.ubaby.dao.ShippingMapper;
import com.ubaby.pojo.Cart;
import com.ubaby.pojo.Order;
import com.ubaby.pojo.OrderItem;
import com.ubaby.pojo.PayInfo;
import com.ubaby.pojo.Product;
import com.ubaby.pojo.Shipping;
import com.ubaby.service.OrderService;
import com.ubaby.util.BigDecimalUtil;
import com.ubaby.util.DateTimeUtil;
import com.ubaby.util.FTPUtil;
import com.ubaby.util.PropertiesUtil;
import com.ubaby.vo.OrderItemVO;
import com.ubaby.vo.OrderProduct;
import com.ubaby.vo.OrderVO;
import com.ubaby.vo.ShippingVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author AlbertRui
 * @date 2018-05-12 18:26
 */
@SuppressWarnings({"JavaDoc", "ResultOfMethodCallIgnored"})
@Service("orderService")
@Transactional
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static AlipayTradeService tradeService;

    static {
        /* 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
           Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /* 使用Configs提供的默认参数
           AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 支付操作
     *
     * @param orderNo
     * @param userId
     * @param path
     * @return
     */
    @Override
    public ServerResponse<Map<String, String>> pay(Long orderNo, Integer userId, String path) {

        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("用户没有该订单");
        resultMap.put("orderNo", order.getOrderNo().toString());

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "ubaby 网上商城扫码支付，订单号：" + outTradeNo;

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "订单" + outTradeNo + "购买商品共" + totalAmount + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
        GoodsDetail goodsDetail;
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        for (OrderItem orderItem : orderItems) {
            goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(), BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), 100d).longValue(), orderItem.getQuantity());
            goodsDetailList.add(goodsDetail);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传文件异常", e);
                }
                logger.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                break;

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return ServerResponse.createByErrorMessage("支付宝预下单失败！！！");
    }

    /**
     * 支付宝回调
     *
     * @param params
     * @return
     */
    @Override
    public ServerResponse<String> alipayCallBack(Map<String, String> params) {

        Long orderNo = Long.valueOf(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("非ubaby网上商城的订单，回调忽略");

        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode())
            return ServerResponse.createBySuccess("支付宝重复回调");

        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();

    }

    /**
     * 查询订单状态
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo) {

        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("用户没有该订单");

        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode())
            return ServerResponse.createBySuccess();

        return ServerResponse.createByError();

    }

    /**
     * 创建订单
     *
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse create(Integer userId, Integer shippingId) {

        //从购物车中获取数据
        List<Cart> carts = cartMapper.selectCheckedCartByUserId(userId);

        //计算订单的总价
        ServerResponse<List<OrderItem>> serverResponse = getCartOrderItem(userId, carts);
        if (!serverResponse.isSuccess()) return serverResponse;

        List<OrderItem> orderItems = serverResponse.getData();
        BigDecimal payment = getOrderTotalPrice(orderItems);
        Order order = assembleOrder(userId, shippingId, payment);
        if (order == null) return ServerResponse.createByErrorMessage("订单生错误");
        if (CollectionUtils.isEmpty(orderItems))
            return ServerResponse.createByErrorMessage("购物车为空");

        for (OrderItem orderItem : orderItems)
            orderItem.setOrderNo(order.getOrderNo());

        //mybatis 批量插入
        orderItemMapper.batchInsert(orderItems);

        //生成成功，减少库存
        reduceProductStock(orderItems);
        //清空购物车
        cleanCarts(carts);

        //把数据返回给前端
        return ServerResponse.createBySuccess(assembleOrderVO(order, orderItems));

    }

    /**
     * 删除订单
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<String> cancel(Integer userId, Long orderNo) {

        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("该用户订单不存在");

        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode())
            return ServerResponse.createByErrorMessage("已付款，无法取消订单");

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

        int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (rowCount > 0) return ServerResponse.createBySuccess();

        return ServerResponse.createByError();

    }

    /**
     * 获取购物车中已经选中的商品
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse getOrderCartProduct(Integer userId) {

        OrderProduct orderProduct = new OrderProduct();
        //从购物车中获取数据
        List<Cart> carts = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse<List<OrderItem>> serverResponse = getCartOrderItem(userId, carts);
        if (!serverResponse.isSuccess())
            return serverResponse;

        List<OrderItem> orderItems = serverResponse.getData();
        List<OrderItemVO> orderItemVoList = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItems) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVO(orderItem));
        }

        orderProduct.setProductTotalPrice(payment);
        orderProduct.setorderItemVoList(orderItemVoList);
        orderProduct.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return ServerResponse.createBySuccess(orderProduct);

    }

    /**
     * 获取订单详情
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<OrderVO> getOrderDetail(Integer userId, Long orderNo) {

        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order != null) {
            List<OrderItem> orderItems = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
            OrderVO orderVO = assembleOrderVO(order, orderItems);
            return ServerResponse.createBySuccess(orderVO);
        }

        return ServerResponse.createByErrorMessage("没有找到该订单");

    }

    /**
     * 个人中心查看订单
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo<OrderVO>> getOrderList(Integer userId, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.selectOrderListByUserId(userId);
        List<OrderVO> orderVOS = assembleOrderVOList(userId, orders);

        PageInfo<OrderVO> pageResult = new PageInfo(orders);
        pageResult.setList(orderVOS);

        return ServerResponse.createBySuccess(pageResult);

    }

    //=========================backend=====================================//

    /**
     * 管理员查看订单
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo<OrderVO>> manageList(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.selectAllOrder();
        List<OrderVO> orderVOS = assembleOrderVOList(null, orders);

        PageInfo<OrderVO> pageResult = new PageInfo(orders);
        pageResult.setList(orderVOS);

        return ServerResponse.createBySuccess(pageResult);

    }

    /**
     * 管理员查看订单详情
     *
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<OrderVO> manageDetail(Long orderNo) {

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(orderNo);
            OrderVO orderVO = assembleOrderVO(order, orderItems);
            return ServerResponse.createBySuccess(orderVO);
        }

        return ServerResponse.createByErrorMessage("订单不存在");

    }

    /**
     * 后台管理员按订单号搜索
     *
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<PageInfo<OrderVO>> manageSearch(Long orderNo, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(orderNo);
            OrderVO orderVO = assembleOrderVO(order, orderItems);

            PageInfo<OrderVO> pageInfo = new PageInfo(Lists.<Order>newArrayList());
            pageInfo.setList(Lists.newArrayList(orderVO));
            return ServerResponse.createBySuccess(pageInfo);
        }

        return ServerResponse.createByErrorMessage("订单不存在");

    }

    /**
     * 后台管理员发货
     *
     * @param orderNo
     * @return
     */
    @Override
    public ServerResponse<String> manageSendGoods(Long orderNo) {

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null && order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {

            order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
            order.setSendTime(new Date());

            orderMapper.updateByPrimaryKeySelective(order);

            return ServerResponse.createBySuccess("发货成功");

        }

        return ServerResponse.createByErrorMessage("订单不存在");

    }

    //=========================private method==============================//

    private List<OrderVO> assembleOrderVOList(Integer userId, List<Order> orders) {

        List<OrderVO> orderVOS = Lists.newArrayList();
        List<OrderItem> orderItems;
        for (Order order : orders) {
            if (userId == null) {
                orderItems = orderItemMapper.selectByOrderNo(order.getOrderNo());
            } else {
                orderItems = orderItemMapper.selectByOrderNoAndUserId(order.getOrderNo(), userId);
            }
            OrderVO orderVO = assembleOrderVO(order, orderItems);
            orderVOS.add(orderVO);
        }

        return orderVOS;

    }

    //组装orderVO
    private OrderVO assembleOrderVO(Order order, List<OrderItem> orderItems) {

        OrderVO orderVO = new OrderVO();
        orderVO.setPayment(order.getPayment());
        orderVO.setOrderNo(order.getOrderNo());
        order.setPaymentType(order.getPaymentType());
        orderVO.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVO.setPostage(order.getPostage());
        orderVO.setStatus(order.getStatus());
        orderVO.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVO.setShippingId(order.getShippingId());

        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVO.setReceiverName(shipping.getReceiverName());
            orderVO.setShippingVO(assembleShippingVO(shipping));
        }

        orderVO.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVO.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVO.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVO.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVO.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));

        orderVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVO> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItems)
            orderItemVoList.add(assembleOrderItemVO(orderItem));

        orderVO.setorderItemVoList(orderItemVoList);

        return orderVO;

    }

    //组装OrderItemVO对象
    private OrderItemVO assembleOrderItemVO(OrderItem orderItem) {
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setOrderNo(orderItem.getOrderNo());
        orderItemVO.setProductId(orderItem.getProductId());
        orderItemVO.setProductName(orderItem.getProductName());
        orderItemVO.setProductImage(orderItem.getProductImage());
        orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVO.setQuantity(orderItem.getQuantity());
        orderItemVO.setTotalPrice(orderItem.getTotalPrice());

        orderItemVO.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVO;
    }

    //组装ShippingVO对象
    private ShippingVO assembleShippingVO(Shipping shipping) {

        ShippingVO shippingVO = new ShippingVO();
        shippingVO.setReceiverAddress(shipping.getReceiverAddress());
        shippingVO.setReceiverCity(shipping.getReceiverCity());
        shippingVO.setReceiverZip(shipping.getReceiverZip());
        shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVO.setReceiverMobile(shipping.getReceiverMobile());
        shippingVO.setReceiverName(shipping.getReceiverName());
        shippingVO.setReceiverPhone(shipping.getReceiverPhone());
        shippingVO.setReceiverProvince(shipping.getReceiverProvince());

        return shippingVO;

    }

    //清空购物车
    private void cleanCarts(List<Cart> carts) {
        for (Cart cart : carts) cartMapper.deleteByPrimaryKey(cart.getId());
    }

    //减库存
    private void reduceProductStock(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    //生成订单
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        Long orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);//全场包邮
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        //发货时间，幅宽时间等等

        int rowCount = orderMapper.insert(order);
        if (rowCount > 0) return order;

        return null;

    }

    //生成订单号
    private Long generateOrderNo() {
        Long currentTime = System.currentTimeMillis();
        return currentTime + (long) new Random().nextInt(100);
    }

    //计算订单总价
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItems) {

        BigDecimal payment = new BigDecimal("0");

        for (OrderItem orderItem : orderItems)
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());

        return payment;

    }

    //获取订单总价
    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> carts) {

        List<OrderItem> orderItems = Lists.newArrayList();
        if (CollectionUtils.isEmpty(carts))
            return ServerResponse.createByErrorMessage("购物车为空");

        //校验购物车的数据，包括产品的状态及数量
        for (Cart cart : carts) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus())
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "不是在线售卖状态");

            //校验库存
            if (cart.getQuantity() > product.getStock())
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
            orderItems.add(orderItem);
        }

        return ServerResponse.createBySuccess(orderItems);

    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

}
