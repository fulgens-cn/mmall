package cn.fulgens.mmall.controller.portal;

import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.common.aop.SystemLog;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IUserService;
import cn.fulgens.mmall.common.utils.CookieUtil;
import cn.fulgens.mmall.common.utils.JsonUtil;
import cn.fulgens.mmall.common.utils.LoginUtil;
import cn.fulgens.mmall.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @SystemLog(value = "用户登陆")
    @PostMapping(value = "/login.do")
    public ServerResponse<User> login(@RequestBody User user, HttpServletResponse response) {
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            return ServerResponse.errorWithMsg("用户名或密码不正确");
        }
        ServerResponse<User> serverResponse = userService.login(user.getUsername(), user.getPassword());
        if (serverResponse.isSuccess()) {
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            CookieUtil.writeLoginToken(response, token);
            RedisUtil.setEx(token, JsonUtil.obj2String(serverResponse.getData()), 30, TimeUnit.MINUTES);
        }
        return serverResponse;
    }

    @SystemLog(value = "用户推出登陆")
    @PostMapping(value = "/logout.do")
    public ServerResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return userService.logout(request, response);
    }

    @PostMapping(value = "/register.do")
    public ServerResponse<String> register(User user) {
        // TODO 对user属性进行校验
        return userService.register(user);
    }

    @PostMapping(value = "/check_valid.do")
    public ServerResponse<String> checkValid(@RequestParam String str,
                                             @RequestParam String type) {
        return userService.checkValid(str, type);
    }

    @PostMapping(value = "/get_user_info.do")
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        User currentUser = LoginUtil.getLoginUser(request);
        if (currentUser != null) {
            return ServerResponse.successWithData(currentUser);
        }
        return ServerResponse.errorWithMsg("用户未登录,无法获取当前用户信息");
    }

    @PostMapping(value = "/forget_get_question.do")
    public ServerResponse<String> forgetGetQuestion(String username) {
        return userService.getQuestionByUsername(username);
    }

    @PostMapping(value = "/forget_check_answer.do")
    public ServerResponse<String> forgetCheckAnswer(String username, String question,
                                                    String answer) {
        return userService.checkAnswer(username, question, answer);
    }

    @PostMapping(value = "/forget_reset_password.do")
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew,
                                                      String forgetToken) {
        return userService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    @PostMapping(value = "/reset_password.do")
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,
                                                HttpServletRequest request) {
        User currentUser = LoginUtil.getLoginUser(request);
        if (currentUser == null) {
            return ServerResponse.errorWithMsg("用户未登录");
        }
        return userService.resetPassword(passwordOld, passwordNew, currentUser);
    }

    @PostMapping(value = "/update_information.do")
    public ServerResponse<User> updateUserInfo(User user, HttpServletRequest request) {
        User currentUser = LoginUtil.getLoginUser(request);
        if (currentUser == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> serverResponse = userService.updateUserInfo(user);
        if (serverResponse.isSuccess()) {
            // 更新用户信息成功，更新redis中的用户信息
            RedisUtil.setEx(CookieUtil.readLoginToken(request), JsonUtil.obj2String(serverResponse.getData()), 30, TimeUnit.MINUTES);
        }
        return serverResponse;
    }

    @PostMapping(value = "/get_information.do")
    public ServerResponse<User> getInformation(HttpServletRequest request) {
        User currentUser = LoginUtil.getLoginUser(request);
        if (currentUser == null) {
            return ServerResponse.buildWithResponseCode(ResponseCode.NEED_LOGIN);
        }
        return userService.getUserInfoById(currentUser.getId());
    }
}
