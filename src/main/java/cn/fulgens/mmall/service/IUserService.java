package cn.fulgens.mmall.service;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
* @Author: fulgens
* @Description: 用户服务接口
* @Date: Created in 2018/2/4 22:41
* @Modified by:
*/
public interface IUserService {

    /**
     * 用户登录
     * @param username  用户名
     * @param password  密码
     * @return
     */
    ServerResponse<User> login(String username, String password);

    /**
     * 用户退出登录
     * @param request
     * @return
     */
    ServerResponse<String> logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * 用户注册
     * @param user
     * @return
     */
    ServerResponse<String> register(User user);

    /**
     * 校验用户名或密码是否已经存在
     * @param str   用户名或密码
     * @param type
     * @return
     */
    ServerResponse<String> checkValid(String str, String type);

    /**
     * 根据用户名获取对应密码提示问题
     * @param username  用户名
     * @return
     */
    ServerResponse<String> getQuestionByUsername(String username);

    /**
     * 校验用户提交的找回密码问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse<String> checkAnswer(String username, String question, String answer);

    /**
     * 用户未登录状态重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    /**
     * 用户登录状态下重置密码
     * @param passwordOld
     * @param passwordNew
     * @param currentUser
     * @return
     */
    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User currentUser);

    /**
     * 登录状态下更新用户信息
     * @param user
     * @return
     */
    ServerResponse<User> updateUserInfo(User user);

    /**
     * 根据用户id获取对应用户详细信息
     * @param userId
     * @return
     */
    ServerResponse<User> getUserInfoById(Integer userId);

    /**
     * 校验用户角色是否为管理员
     * @param user
     * @return
     */
    ServerResponse checkAdminRole(User user);
}
