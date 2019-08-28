package cn.fulgens.mmall.controller.backend;

import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IOrderService;
import cn.fulgens.mmall.service.IUserService;
import cn.fulgens.mmall.common.utils.LoginUtil;
import cn.fulgens.mmall.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/manage/order/")
public class OrderManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService orderService;

    /**
     * 获取订单列表
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "list.do")
    public ServerResponse<com.github.pagehelper.PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                                               @RequestParam(value = "pageSize", defaultValue = "10")int pageSize,
                                                               HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 获取订单分页列表
            return orderService.manageOrderList(pageNum, pageSize);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    /**
     * 根据订单编号获取订单详情
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping(value = "detail.do")
    public ServerResponse<OrderVo> getOrderDetail(Long orderNo, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 获取订单详情
            return orderService.manageOrderDetail(orderNo);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    /**
     * 订单发货
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping(value = "send_goods.do")
    public ServerResponse<String> sendGoods(Long orderNo, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 订单发货
            return orderService.manageSendGoods(orderNo);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    /**
     * 根据订单号搜索
     * @param orderNo
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "")
    public ServerResponse<PageInfo> search(Long orderNo, HttpServletRequest request,
                                           @RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "1")int pageSize) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 订单搜索
            return orderService.manageSearchOrder(orderNo, pageNum, pageSize);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }
}
