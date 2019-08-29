package cn.fulgens.mmall.controller.backend;

import cn.fulgens.mmall.common.Constants;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.common.utils.CookieUtil;
import cn.fulgens.mmall.common.utils.JsonUtil;
import cn.fulgens.mmall.common.utils.RedisUtil;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/manage/user")
public class UserManageController {

    @Autowired
    private IUserService userService;

    @PostMapping(value = "/login.do")
    public ServerResponse<User> login(String username, String password,
                                      HttpServletResponse response) {
        ServerResponse<User> serverResponse = userService.login(username, password);
        if (serverResponse.isSuccess()) {
            User user = serverResponse.getData();
            if (user.getRole() == Constants.Role.ROLE_ADMIN) {
                String token = UUID.randomUUID().toString().replaceAll("-", "");
                CookieUtil.writeLoginToken(response, token);
                RedisUtil.setEx(token, JsonUtil.obj2String(user), 30, TimeUnit.MINUTES);
            }else {
                return ServerResponse.errorWithMsg("非管理员无权登录后台");
            }
        }
        return serverResponse;
    }

}
