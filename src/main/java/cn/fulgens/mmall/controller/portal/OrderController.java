package cn.fulgens.mmall.controller.portal;

import cn.fulgens.mmall.common.Constants;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IOrderService;
import cn.fulgens.mmall.common.utils.LoginUtil;
import cn.fulgens.mmall.vo.OrderProductVo;
import cn.fulgens.mmall.vo.OrderVo;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService orderService;

    /**
     * 创建订单
     * @param shippingId
     * @param request
     * @return
     */
    @RequestMapping("create.do")
    public ServerResponse<OrderVo> createOrder(Integer shippingId, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return orderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 根据订单编号取消订单
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("cancel.do")
    public ServerResponse<String> cancelOrder(Long orderNo, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return orderService.cancelOrder(user.getId(), orderNo);
    }

    /**
     * 确认订单前预览
     * @param request
     * @return
     */
    @RequestMapping("get_order_cart_product.do")
    public ServerResponse<OrderProductVo> getOrderCartProduct(HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return orderService.getOrderCartProduct(user.getId());
    }

    /**
     * 用户查看订单详情
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("detail.do")
    public ServerResponse<OrderVo> detail(Long orderNo, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return orderService.getOrderDetail(user.getId(), orderNo);
    }

    /**
     * 订单列表
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping("list.do")
    public ServerResponse<com.github.pagehelper.PageInfo> detail(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                                 HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return orderService.getOrderList(user.getId(), pageNum, pageSize);
    }

    /**
     * 订单支付
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping(value = "pay.do")
    public ServerResponse pay(Long orderNo, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return orderService.pay(user.getId(), orderNo, path);
    }

    /**
     * 支付宝当面付异步通知处理
     * @param request
     * @return
     */
    @RequestMapping(value = "alipay_callback.do")
    public Object alipayCallback(HttpServletRequest request) {
        // 获取当面付异步通知参数列表
        Map<String, String> params = Maps.newHashMap();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<String> keySet = parameterMap.keySet();
        for (String key : keySet) {
            String[] values = parameterMap.get(key);
            String value = "";
            for (int i = 0; i < values.length; i++) {
                value += (i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(key, value);
        }

        // 在通知返回参数列表中，除去sign、sign_type两个参数,其它参数皆是待验签的参数
        // params.remove("sign");   // 此参数在AlipaySignature类getSignCheckContentV2方法中已移除
        params.remove("sign_type");
        try {
            // 调用AlipaySignature的rsaCheckV2方法验签
            boolean rsaCheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(), "utf-8", Configs.getSignType());
            if (!rsaCheckV2) {
                return ServerResponse.errorWithMsg("异常通知,验证签名未通过");
            }
            // 校验通知数据的正确性
            ServerResponse checkResponse = orderService.checkAlipayNotifyData(params);
            if (checkResponse.isSuccess()) {
                // 通知数据正确
                ServerResponse serverResponse = orderService.doAlipayNotify(params);
                if (serverResponse.isSuccess()) {
                    return Constants.AlipayCallback.RESPONSE_SUCCESS;
                }
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝签证签名异常", e);
        }
        return Constants.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询订单支付状态
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping(value = "query_order_pay_status.do")
    public ServerResponse<Boolean> queryOrderPayStatus(long orderNo, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }

        ServerResponse serverResponse = orderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.successWithData(true);
        }
        return ServerResponse.successWithData(false);
    }

}
