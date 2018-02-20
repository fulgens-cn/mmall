package cn.fulgens.mmall.service;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.vo.OrderProductVo;
import cn.fulgens.mmall.vo.OrderVo;
import com.github.pagehelper.PageInfo;

import java.util.Map;

public interface IOrderService {

    /**
     * 订单支付
     * @param userId 用户id
     * @param orderNo 订单编号
     * @param path 支付宝扫码支付二维码图片本地保存路径
     * @return
     */
    ServerResponse pay(Integer userId, Long orderNo, String path);

    /**
     * 校验支付宝当面付异步通知数据的正确性
     * @param params 异步通知请求参数
     * @return
     */
    ServerResponse checkAlipayNotifyData(Map<String, String> params);

    /**
     * 异步通知数据验证正确后的系统逻辑（修改订单状态、生成支付信息）
     * @param params 异步通知请求参数
     * @return
     */
    ServerResponse doAlipayNotify(Map<String, String> params);

    /**
     * 查询订单状态
     * @param userId 用户id
     * @param orderNo 订单编号
     * @return
     */
    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    /**
     * 创建订单
     * @param userId 用户id
     * @param shippingId 收货地址id
     * @return
     */
    ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId);

    /**
     * 取消订单
     * @param userId 用户id
     * @param orderNo 订单编号
     * @return
     */
    ServerResponse<String> cancelOrder(Integer userId, Long orderNo);

    /**
     * 购物车结算生成订单预览
     * @param userId 用户id
     * @return
     */
    ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId);

    /**
     * 获取订单详情
     * @param userId 用户id
     * @param orderNo 订单编号
     * @return
     */
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    /**
     * 获取用户订单列表
     * @param userId 用户id
     * @param pageNum 当前页页码
     * @param pageSize 每页显示条数
     * @return
     */
    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    /**
     * 获取订单分页列表
     * @param pageNum 当前页页码
     * @param pageSize 每页显示条数
     * @return
     */
    ServerResponse<PageInfo> manageOrderList(int pageNum, int pageSize);

    /**
     * 获取订单详情
     * @param orderNo 订单编号
     * @return
     */
    ServerResponse<OrderVo> manageOrderDetail(Long orderNo);

    /**
     * 订单发货
     * @param orderNo 订单编号
     * @return
     */
    ServerResponse<String> manageSendGoods(Long orderNo);

    /**
     * 根据订单号搜索相应订单
     * @param orderNo 订单编号
     * @param pageNum 当前页页码
     * @param pageSize 每页显示条数
     * @return
     */
    ServerResponse<PageInfo> manageSearchOrder(Long orderNo, int pageNum, int pageSize);
}
