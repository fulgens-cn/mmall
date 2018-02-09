package cn.fulgens.mmall.controller.portal;

import cn.fulgens.mmall.common.Const;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/user/")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping(value = "login.do")
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> serverResponse = userService.login(username, password);
        if (serverResponse.isSuccess()) {
            // 用户登录成功，将用户对象放入session域
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        }
        return serverResponse;
    }

    @PostMapping(value = "logout.do")
    public ServerResponse<String> logout(HttpSession session) {
        return userService.logout(session);
    }

    @PostMapping(value = "register.do")
    public ServerResponse<String> register(User user) {
        // TODO 对user属性进行校验
        return userService.register(user);
    }

    @PostMapping(value = "check_valid.do")
    public ServerResponse<String> checkValid(@RequestParam String str,
                                             @RequestParam String type) {
        return userService.checkValid(str, type);
    }

    @PostMapping(value = "get_user_info.do")
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.successWithData(user);
        }
        return ServerResponse.errorWithMsg("用户未登录,无法获取当前用户信息");
    }

    @PostMapping(value = "forget_get_question.do")
    public ServerResponse<String> forgetGetQuestion(String username) {
        return userService.getQuestionByUsername(username);
    }

    @PostMapping(value = "forget_check_answer.do")
    public ServerResponse<String> forgetCheckAnswer(String username, String question,
                                                    String answer) {
        return userService.checkAnswer(username, question, answer);
    }

    @PostMapping(value = "forget_reset_password.do")
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew,
                                                      String forgetToken) {
        return userService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    @PostMapping(value = "reset_password.do")
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,
                                                HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.errorWithMsg("用户未登录");
        }
        return userService.resetPassword(passwordOld, passwordNew, currentUser);
    }

    @PostMapping(value = "update_information.do")
    public ServerResponse<User> updateUserInfo(User user, HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.errorWithMsg("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> serverResponse = userService.updateUserInfo(user);
        if (serverResponse.isSuccess()) {
            // 更新用户信息成功，更新session中的用户信息
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        }
        return serverResponse;
    }

    @PostMapping(value = "get_information.do")
    public ServerResponse<User> getInformation(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.errorWithMsg(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录,无法获取当前用户信息,status=10,强制登录");
        }
        return userService.getUserInfoById(currentUser.getId());
    }
}
