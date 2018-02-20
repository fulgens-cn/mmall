package cn.fulgens.mmall.controller.backend;

import cn.fulgens.mmall.common.Const;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IOrderService;
import cn.fulgens.mmall.service.IUserService;
import cn.fulgens.mmall.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/manage/order/")
public class OrderManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService orderService;

    // 获取订单列表
    @RequestMapping(value = "list.do")
    public ServerResponse<com.github.pagehelper.PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                                               @RequestParam(value = "pageSize", defaultValue = "10")int pageSize,
                                                               HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.errorWithMsg(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 获取订单分页列表
            return orderService.manageOrderList(pageNum, pageSize);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    // 根据订单编号获取订单详情
    @RequestMapping(value = "detail.do")
    public ServerResponse<OrderVo> getOrderDetail(Long orderNo, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.errorWithMsg(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 获取订单详情
            return orderService.manageOrderDetail(orderNo);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    // 订单发货
    @RequestMapping(value = "send_goods.do")
    public ServerResponse<String> sendGoods(Long orderNo, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.errorWithMsg(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 订单发货
            return orderService.manageSendGoods(orderNo);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }

    // 根据订单号搜索
    @RequestMapping(value = "")
    public ServerResponse<PageInfo> search(Long orderNo, HttpSession session,
                                           @RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "1")int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.errorWithMsg(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            // 订单搜索
            return orderService.manageSearchOrder(orderNo, pageNum, pageSize);
        }else {
            return ServerResponse.errorWithMsg("无权限执行操作，需要管理员权限");
        }
    }
}
