package cn.fulgens.mmall.utils;

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
}
