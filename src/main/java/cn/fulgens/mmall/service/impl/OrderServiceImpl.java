package cn.fulgens.mmall.service.impl;

import cn.fulgens.mmall.common.Constants;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.common.utils.*;
import cn.fulgens.mmall.mapper.*;
import cn.fulgens.mmall.pojo.*;
import cn.fulgens.mmall.service.IOrderService;
import cn.fulgens.mmall.vo.OrderItemVo;
import cn.fulgens.mmall.vo.OrderProductVo;
import cn.fulgens.mmall.vo.OrderVo;
import cn.fulgens.mmall.vo.ShippingVo;
import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private AlipayTradeService alipayTradeService;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId) {
        // 参数校验
        if (userId == null || shippingId == null) {
            return  ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByIdAndUserId(shippingId, userId);
        if (shipping == null) {
            return ServerResponse.errorWithMsg("收货地址不存在");
        }
        // 根据用户id获取购物车列表
        List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);
        // 创建订单实体
        Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        // 生成订单项列表
        List<OrderItem> orderItemList = null;
        ServerResponse<List<OrderItem>> serverResponse = this.getOrderItemList(userId, cartList, orderNo);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.errorWithMsg(serverResponse.getMsg());
        }
        orderItemList = serverResponse.getData();
        // MyBatis批量插入订单项
        orderItemMapper.batchInsert(orderItemList);

        // 计算订单总价
        BigDecimal payment = this.getOrderPayment(orderItemList);
        // 设置订单总价
        order.setPayment(payment);
        // 设置订单所属用户id
        order.setUserId(userId);
        // 设置订单状态为未付款状态
        order.setStatus(Constants.OrderStatusEnum.NO_PAY.getCode());
        // 社会自订单支付类型为在线支付
        order.setPaymentType(Constants.PaymentTypeEnum.ONLINE_PAY.getCode());
        // 设置订单邮费为0元
        order.setPostage(BigDecimal.ZERO);
        // 设置订单收货地址id
        order.setShippingId(shippingId);
        int rowCount = orderMapper.insert(order);
        if (rowCount <= 0) {
            return ServerResponse.errorWithMsg("生成订单失败");
        }

        // 清空购物车
        this.cleanCart(cartList);
        // 减少产品库存
        this.reduceProductStock(orderItemList);

        return ServerResponse.successWithData(assembleOrderVo(order, orderItemList));
    }

    @Override
    public ServerResponse<String> cancelOrder(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.errorWithMsg("该用户不存在编号为" + orderNo + "的相应订单");
        }
        // 校验订单状态
        if (order.getStatus() != Constants.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.errorWithMsg("订单已支付，无法取消");
        }
        // 更新订单状态为已取消
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Constants.OrderStatusEnum.CANCELED.getCode());
        int rowCount = orderMapper.updateByPrimaryKeySelective(order);
        if (rowCount > 0) {
            return ServerResponse.success();
        }
        return ServerResponse.error();
    }

    @Override
    public ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId) {
        // 获取用户已勾选产品购物车列表
        List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);
        // 获取订单项列表
        ServerResponse<List<OrderItem>> serverResponse = this.getOrderItemList(userId, cartList, null);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.errorWithMsg("");
        }
        List<OrderItem> orderItemList = serverResponse.getData();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        OrderProductVo orderProductVo = new OrderProductVo();
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setProductTotalPrice(getOrderPayment(orderItemList));
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.successWithData(orderProductVo);
    }

    @Override
    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.errorWithMsg("该用户不存在编号为" + orderNo + "的相应订单");
        }
        // 根据订单获取相应订单项集合
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, orderNo);
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.successWithData(orderVo);
    }

    @Override
    public ServerResponse<com.github.pagehelper.PageInfo> getOrderList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);
        com.github.pagehelper.PageInfo pageInfo = new com.github.pagehelper.PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.successWithData(pageInfo);
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList) {
            List<OrderItem> orderItemList = Lists.newArrayList();
            if (userId == null) {
                // 管理员查询
                orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
            }else {
                orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, order.getOrderNo());
            }
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    // backend
    @Override
    public ServerResponse<com.github.pagehelper.PageInfo> manageOrderList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAll();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);
        com.github.pagehelper.PageInfo pageInfo = new com.github.pagehelper.PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.successWithData(pageInfo);
    }

    @Override
    public ServerResponse<OrderVo> manageOrderDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.errorWithMsg("不存在此订单，请核对订单号");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.successWithData(orderVo);
    }

    @Override
    public ServerResponse<String> manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.errorWithMsg("不存在此订单，请核对订单号");
        }
        // 校验订单状态
        if (order.getStatus() < Constants.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.errorWithMsg("订单尚未完成支付，无法发货");
        }
        if (order.getStatus() == Constants.OrderStatusEnum.PAID.getCode()) {
            // 更新订单状态为已发货
            order.setStatus(Constants.OrderStatusEnum.SHIPPED.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
            return ServerResponse.successWithMsg("发货成功");
        }
        return ServerResponse.errorWithMsg("发货失败");
    }

    @Override
    public ServerResponse<PageInfo> manageSearchOrder(Long orderNo, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.errorWithMsg("不存在此订单，请核对订单号");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
        pageInfo.setList(Lists.newArrayList(orderVo));
        return ServerResponse.successWithData(pageInfo);
    }

    // 生成订单号
    private long generateOrderNo() {
        return System.currentTimeMillis() + new Random(100).nextInt();
    }

    private ServerResponse<List<OrderItem>> getOrderItemList(Integer userId, List<Cart> cartList, Long orderNo) {
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.errorWithMsg("购物车为空");
        }
        List<OrderItem> orderItemList = Lists.newArrayList();
        for (Cart cart : cartList) {
            // 校验产品销售状态
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product.getStatus() != Constants.ProductStatusEnum.ON_SALE.getCode()) {
                return ServerResponse.errorWithMsg("产品" + product.getName() +"已下架或已删除,下单失败");
            }
            // 校验产品库存是否充足
            if (product.getStock() < cart.getQuantity()) {
                return ServerResponse.errorWithMsg("产品" + product.getName() + "库存不足，下单失败");
            }
            // 封装OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(userId);
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(cart.getProductId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.successWithData(orderItemList);
    }

    // 根据订单项集合计算订单总价
    private BigDecimal getOrderPayment(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal(0);
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    // 清空购物车
    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    // 减少产品库存
    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Constants.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Constants.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(JodaTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(JodaTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(JodaTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(JodaTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(JodaTimeUtil.dateToStr(order.getCloseTime()));


        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));


        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }


    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getProductPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(JodaTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }

    @Override
    public ServerResponse pay(Integer userId, Long orderNo, String path) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.errorWithMsg("系统中未查询到该用户编号为" + orderNo + "的订单记录");
        }
        Map<String, String> resultMap = Maps.newHashMap();
        resultMap.put("orderNo", orderNo + "");

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "mmall " + order.getOrderNo() + "订单";

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
        int productCount = orderItemMapper.selectProductCountByUserIdAndOrderNo(userId, orderNo);
        String body = "购买mmall商城商品" + productCount + "件,共" + totalAmount + "元";

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
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, orderNo);
        for (OrderItem orderItem : orderItemList) {
            GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getProductPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goodsDetail);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.notify.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = alipayTradeService.tradePrecreate(builder);
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
                FTPUtil.uploadFile("img", targetFile);

                logger.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                // 返回当面付二维码访问路径
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.successWithData(resultMap);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.errorWithMsg("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.errorWithMsg("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.errorWithMsg("不支持的交易状态，交易返回异常!!!");
        }
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

    @Override
    public ServerResponse checkAlipayNotifyData(Map<String, String> params) {
        // 验证通知数据中的out_trade_no是否为商户系统中创建的订单号
        String orderNo = params.get("out_trade_no");
        Order order = orderMapper.selectByOrderNo(Long.valueOf(orderNo));
        if (order == null) {
            return ServerResponse.errorWithMsg("非mmall商城系统订单,验证失败");
        }
        // 判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额）
        String total_amount = params.get("total_amount");   // 单位元
        if (!StringUtils.equals(order.getPayment().toString(), total_amount)) {
            return ServerResponse.errorWithMsg("订单金额不一致，验证失败");
        }
        // 校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方
        String seller_id = params.get("seller_id");
        if (!StringUtils.equals(Configs.getPid(), seller_id)) {
            return ServerResponse.errorWithMsg("卖家支付宝用户号不一致，验证失败");
        }
        return ServerResponse.successWithMsg("当面付异步通知数据验证通过");
    }

    @Override
    public ServerResponse doAlipayNotify(Map<String, String> params) {
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order.getStatus() >= Constants.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.successWithMsg("支付宝重复调用");
        }
        if(Constants.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(JodaTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Constants.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Constants.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);

        return ServerResponse.success();
    }

    @Override
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.errorWithMsg("系统中未查询到该用户编号为" + orderNo + "的订单记录");
        }
        if (order.getStatus() > Constants.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.success();
        }
        return ServerResponse.error();
    }

    @Override
    public void closeOrder(long timeout, ChronoUnit unit) {
        // 查询指定时间内未支付订单
        LocalDateTime time = LocalDateTime.now().minus(timeout, unit);
        String closeTime = time.format(DateTimeFormatter.ofPattern(JodaTimeUtil.STANDARD_PATTERN));
        List<Order> orderList = orderMapper.selectByStatusAndCreateTime(Constants.OrderStatusEnum.NO_PAY.getCode(), closeTime);
        if (CollectionUtils.isEmpty(orderList)) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        orderList.stream().forEach(order -> {
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
            // 订单项商品库存还原
            orderItemList.stream().forEach(orderItem -> {
                productMapper.incrProductStock(orderItem.getQuantity());
            });
            // 关闭订单
            Order updatedOrder = new Order();
            updatedOrder.setId(order.getId());
            updatedOrder.setStatus(Constants.OrderStatusEnum.ORDER_CLOSE.getCode());
            orderMapper.updateByPrimaryKeySelective(updatedOrder);
        });
    }
}
