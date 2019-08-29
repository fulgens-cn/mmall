package cn.fulgens.mmall.controller.backend;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.service.IOrderService;
import cn.fulgens.mmall.service.IUserService;
import cn.fulgens.mmall.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService orderService;

    /**
     * 获取订单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list.do")
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10")int pageSize) {
        return orderService.manageOrderList(pageNum, pageSize);
    }

    /**
     * 根据订单编号获取订单详情
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/detail.do")
    public ServerResponse<OrderVo> getOrderDetail(Long orderNo, HttpServletRequest request) {
        return orderService.manageOrderDetail(orderNo);
    }

    /**
     * 订单发货
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/send_goods.do")
    public ServerResponse<String> sendGoods(Long orderNo, HttpServletRequest request) {
        return orderService.manageSendGoods(orderNo);
    }

    /**
     * 根据订单号搜索
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/")
    public ServerResponse<PageInfo> search(Long orderNo,
                                           @RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "1")int pageSize) {
        return orderService.manageSearchOrder(orderNo, pageNum, pageSize);
    }
}
