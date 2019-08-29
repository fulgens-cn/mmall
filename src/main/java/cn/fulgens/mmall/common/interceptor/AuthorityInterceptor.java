package cn.fulgens.mmall.common.interceptor;

import cn.fulgens.mmall.common.ServerResponse;
import cn.fulgens.mmall.common.SimditorServerResponse;
import cn.fulgens.mmall.common.utils.LoginUtil;
import cn.fulgens.mmall.pojo.User;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 后台管理接口权限校验拦截器
 *
 * @author fulgens
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String url = request.getRequestURI();
        if (isAdminLoginUrl(url)) {
            return true;
        }
        User currentUser = LoginUtil.getLoginUser(request);
        if (currentUser == null) {
            if (isSimditorUploadUrl(url)) {
                writeErrorMsg(response, SimditorServerResponse.error("请登录管理员账户", null));
            } else {
                writeErrorMsg(response, ServerResponse.errorWithMsg("请登录管理员账户"));
            }
            return false;
        }
        if (!LoginUtil.isAdmin(currentUser)) {
            if (isSimditorUploadUrl(url)) {
                writeErrorMsg(response, SimditorServerResponse.error("权限不足", null));
            } else {
                writeErrorMsg(response, ServerResponse.errorWithMsg("权限不足"));
            }
            return false;
        }
        return true;
    }

    private boolean isAdminLoginUrl(String requestUrl) {
        return StringUtils.equals("/manage/user/login.do", requestUrl);
    }

    private boolean isSimditorUploadUrl(String requestUrl) {
        return StringUtils.equals("/manage/product/richtext_img_upload.do", requestUrl);
    }

    private void writeErrorMsg(HttpServletResponse response, Object obj) throws IOException {
        response.reset();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(obj));
        writer.flush();
        writer.close();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {

    }
}
