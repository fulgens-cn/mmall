package cn.fulgens.mmall.common.filter;

import cn.fulgens.mmall.pojo.User;
import cn.fulgens.mmall.common.utils.CookieUtil;
import cn.fulgens.mmall.common.utils.JsonUtil;
import cn.fulgens.mmall.common.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Redis Session过期时间重置过滤器
 *
 * @author fulgens
 */
public class SessionExpireFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(token)) {
            User currentUser = JsonUtil.string2Obj(RedisUtil.get(token), User.class);
            if (currentUser != null) {
                RedisUtil.expire(token, 30, TimeUnit.MINUTES);
            }
        }
        filterChain.doFilter(request, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
