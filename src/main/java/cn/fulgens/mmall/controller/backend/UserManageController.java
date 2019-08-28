package cn.fulgens.mmall.controller.backend;

import cn.fulgens.mmall.common.Constants;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/manage/user/")
public class UserManageController {

    @Autowired
    private IUserService userService;

    @PostMapping(value = "login.do")
    public ServerResponse<User> login(String username, String password,
                                      HttpSession session) {
        ServerResponse<User> serverResponse = userService.login(username, password);
        if (serverResponse.isSuccess()) {
            User user = serverResponse.getData();
            if (user.getRole() == Constants.Role.ROLE_ADMIN) {
                // 管理员登录
                session.setAttribute(Constants.CURRENT_USER, user);
            }else {
                return ServerResponse.errorWithMsg("非管理员无权登录后台");
            }
        }
        return serverResponse;
    }



}
