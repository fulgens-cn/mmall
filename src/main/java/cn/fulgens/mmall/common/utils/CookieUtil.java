package cn.fulgens.mmall.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * Cookie工具类
 *
 * @author fulgens
 */
@Slf4j
public class CookieUtil {

    private final static String COOKIE_DOMAIN = "fulgens.cn";

    private final static String COOKIE_NAME_TOKEN = "token";

    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                log.info("read cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME_TOKEN)) {
                    log.info("return cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // 如果是-1，代表永久 单位是秒
        // 如果这个maxAge不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效
        cookie.setMaxAge(60 * 30);
        log.info("write cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
        response.addCookie(cookie);
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME_TOKEN)) {
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    log.info("del cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
