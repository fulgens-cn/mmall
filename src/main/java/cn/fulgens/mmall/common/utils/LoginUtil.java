package cn.fulgens.mmall.common.utils;

import cn.fulgens.mmall.common.Constants;
import cn.fulgens.mmall.pojo.User;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户登陆工具类
 *
 * @author fulgens
 */
public class LoginUtil {

    public static User getLoginUser(HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return JsonUtil.string2Obj(RedisUtil.get(token), User.class);
    }

    public static boolean isAdmin(User currentUser) {
        if (currentUser == null || currentUser.getRole() == null) {
            return false;
        }
        if (Constants.Role.ROLE_ADMIN != currentUser.getRole()) {
            return false;
        }
        return true;
    }
}
