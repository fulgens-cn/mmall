package cn.fulgens.mmall.service.impl;

import cn.fulgens.mmall.common.Const;
import cn.fulgens.mmall.common.ResponseCode;
import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.common.TokenCache;
import cn.fulgens.mmall.dao.UserMapper;
import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.service.IUserService;
import cn.fulgens.mmall.utils.CookieUtil;
import cn.fulgens.mmall.utils.MD5Util;
import cn.fulgens.mmall.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
* @Author: fulgens
* @Description: 用户服务实现类
* @Date: Created in 2018/2/4 22:41
* @Modified by:
*/
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        // 根据用户名查询对应用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return ServerResponse.errorWithMsg("用户名或密码错误");
        }
        // 对密码进行MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        // 比对用户密码
        if (!md5Password.equals(user.getPassword())) {
            return ServerResponse.errorWithMsg("用户名或密码错误");
        }
        // 登录成功，将user对象中的密码置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.successWithMsgAndData("登录成功", user);
    }

    @Override
    public ServerResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(token)) {
            return ServerResponse.errorWithMsg("用户未登录无法退出登陆");
        }
        CookieUtil.delLoginToken(request, response);
        redisUtil.delete(token);
        return ServerResponse.successWithMsg("退出登陆成功");
    }

    @Override
    public ServerResponse<String> register(User user) {
        // 校验用户名是否已经存在
        ServerResponse<String> checkValid = checkValid(user.getUsername(), Const.USERANME);
        if (!checkValid.isSuccess()) {
            return ServerResponse.errorWithMsg("用户名已存在");
        }
        // 校验邮箱是否已经存在
        checkValid = checkValid(user.getEmail(), Const.EMAIL);
        if (!checkValid.isSuccess()) {
            return ServerResponse.errorWithMsg("用户邮箱已存在");
        }
        // 设置用户角色，管理员-0，普通用户-1
        user.setRole(Const.Role.ROLE_USER);
        // 对密码进行md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        // 插入用户数据
        int result = userMapper.insert(user);
        if (result == 0) {
            return ServerResponse.errorWithMsg("注册失败");
        }
        return ServerResponse.successWithMsg("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (type.equals(Const.USERANME)) {
                // 校验用户名是否已经存在
                int count = userMapper.selectCountByUsername(str);
                if (count > 0) {
                    return ServerResponse.errorWithMsg("用户名已存在");
                }
            }
            if (type.equals(Const.EMAIL)) {
                // 校验邮箱是否已经存在
                int count = userMapper.selectCountByEmail(str);
                if (count > 0) {
                    return ServerResponse.errorWithMsg("用户邮箱已存在");
                }
            }
        }else {
            return ServerResponse.errorWithMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数不合法");
        }
        return ServerResponse.successWithMsg("校验通过");
    }

    @Override
    public ServerResponse<String> getQuestionByUsername(String username) {
        // 校验用户名是否存在
        ServerResponse<String> validResponse = checkValid(username, Const.USERANME);
        if (validResponse.isSuccess()) {
            return ServerResponse.errorWithMsg("用户名不存在");
        }
        String question = userMapper.getQuestionByUsername(username);
        if (StringUtils.isBlank(question)) {
            return ServerResponse.errorWithMsg("该用户未设置找回密码问题");
        }
        return ServerResponse.successWithData(question);
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        // 校验用户名是否存在
        ServerResponse<String> validResponse = checkValid(username, Const.USERANME);
        if (validResponse.isSuccess()) {
            return ServerResponse.errorWithMsg("用户名不存在");
        }
        // 校验用户提交的忘记密码问题答案
        int count = userMapper.checkAnswer(username, question, answer);
        if (count > 0) {
            // 用户提交的忘记密码问题答案正确
            String token = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, token);
            return ServerResponse.successWithData(token);
        }
        return ServerResponse.errorWithMsg("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        // 校验用户名是否存在
        ServerResponse<String> validResponse = checkValid(username, Const.USERANME);
        if (validResponse.isSuccess()) {
            return ServerResponse.errorWithMsg("用户名不存在");
        }
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.errorWithMsg("参数错误，token不能为空");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.errorWithMsg("token无效或已过期");
        }
        if (!StringUtils.equals(token, forgetToken)) {
            return ServerResponse.errorWithMsg("token不一致");
        }
        // 对新密码进行MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
        // 更新用户密码
        int count = userMapper.updatePasswordByUsername(username, md5Password);
        if (count > 0) {
            return ServerResponse.successWithMsg("密码修改成功");
        }
        return ServerResponse.errorWithMsg("密码修改失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User currentUser) {
        // 对旧密码进行校验，防止用户横向越权
        int count = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), currentUser.getId());
        if (count == 0) {
            return ServerResponse.errorWithMsg("旧密码错误");
        }
        // 对新密码进行md5加密
        String md5PasswordNew = MD5Util.MD5EncodeUtf8(passwordNew);
        // 重置密码
        currentUser.setPassword(md5PasswordNew);
        count = userMapper.updateByPrimaryKeySelective(currentUser);
        if (count > 0) {
            return ServerResponse.successWithMsg("密码重置成功");
        }
        return ServerResponse.errorWithMsg("密码重置失败");
    }

    @Override
    public ServerResponse<User> updateUserInfo(User user) {
        // 校验邮箱是否已经存在，但要排除用户自己的邮箱
        int count = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (count > 0) {
            return ServerResponse.errorWithMsg("email已存在，请更换email再重新尝试");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(user.getUsername());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        // 更新用户信息
        count = userMapper.updateByPrimaryKeySelective(updateUser);
        if (count > 0) {
            return ServerResponse.successWithMsgAndData("更新用户信息成功", updateUser);
        }
        return ServerResponse.errorWithMsg("更新用户信息失败");
    }

    @Override
    public ServerResponse<User> getUserInfoById(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.errorWithMsg("找不到对应用户");
        }
        // 将密码置空
        user.setPassword("");
        return ServerResponse.successWithData(user);
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.success();
        }
        return ServerResponse.error();
    }
}
