package cn.fulgens.mmall.controller.portal;

import cn.fulgens.mmall.common.Const;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.Shipping;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IShippingService;
import cn.fulgens.mmall.utils.LoginUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/shipping")
public class ShippingController {

    @Autowired
    private IShippingService shippingService;

    @RequestMapping(value = "/add.do")
    public ServerResponse add(Shipping shipping, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return shippingService.add(user.getId(), shipping);
    }

    @RequestMapping(value = "/del.do")
    public ServerResponse del(Integer shippingId, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return shippingService.delete(user.getId(), shippingId);
    }

    @RequestMapping(value = "update.do")
    public ServerResponse update(Shipping shipping, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return shippingService.update(user.getId(), shipping);
    }

    @RequestMapping(value = "select.do")
    public ServerResponse<Shipping> select(Integer shippingId, HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return shippingService.get(user.getId(), shippingId);
    }

    @RequestMapping(value = "list.do")
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpServletRequest request) {
        User user = LoginUtil.getLoginUser(request);
        if (user == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return shippingService.list(user.getId(), pageNum, pageSize);
    }

}
