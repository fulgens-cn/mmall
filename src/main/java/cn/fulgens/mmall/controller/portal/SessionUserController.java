package cn.fulgens.mmall.controller.portal;

import cn.fulgens.mmall.common.Constants;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.common.aop.SystemLog;
import cn.fulgens.mmall.common.utils.CookieUtil;
import cn.fulgens.mmall.common.utils.JsonUtil;
import cn.fulgens.mmall.common.utils.LoginUtil;
import cn.fulgens.mmall.common.utils.RedisUtil;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * SpringSession实现session共享测试Controller
 *
 * @author fulgens
 */
@Slf4j
@RestController
@RequestMapping(value = "/user/session/")
public class SessionUserController {

    @Autowired
    private IUserService userService;

    @PostMapping(value = "login.do")
    public ServerResponse<User> login(@RequestBody User user, HttpSession session) {
        ServerResponse<User> serverResponse = userService.login(user.getUsername(), user.getPassword());
        if (serverResponse.isSuccess()) {
            session.setAttribute(Constants.CURRENT_USER, serverResponse.getData());
        }
        return serverResponse;
    }

    @PostMapping(value = "logout.do")
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Constants.CURRENT_USER);
        return ServerResponse.successWithMsg("退出登录成功");
    }

    @PostMapping(value = "get_user_info.do")
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User currentUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (currentUser != null) {
            return ServerResponse.successWithData(currentUser);
        }
        return ServerResponse.errorWithMsg("用户未登录,无法获取当前用户信息");
    }
}
