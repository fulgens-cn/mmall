package cn.fulgens.mmall.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;

/**
 * 继承WebApplicationInitializer的基础实现AbstractAnnotationConfigDispatcherServletInitializer
 * 实现对DispatcherServlet及ContextLoaderListenser的java配置
 */
public class WebAppInitializer
        extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebMvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // 添加字符编码过滤器，解决post提交乱码问题
        FilterRegistration.Dynamic characterEncodingFilter =
                servletContext.addFilter("characterEncodingFilter", CharacterEncodingFilter.class);
        // 通过初始化参数设置编码为UTF-8
        characterEncodingFilter.setInitParameter("encoding", "UTF-8");
        // 设置强制编码
        characterEncodingFilter.setInitParameter("forceEncoding", "true");
        // 设置UrlPattern
        characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");
        super.onStartup(servletContext);
    }

    /*@Override
    // 使用StandardServletMultipartResolver处理文件上传时
    // 通过重载customizeRegistration方法设置servlet3.0对multipart的支持
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        // 创建MultipartConfigElement对象，配置对上传文件的限制
        // 参数1：location临时目录（必须）
        // 参数2：maxFileSize上传文件的最大容量（单位：字节），默认无限制
        // 参数3：maxRequestSize整个multipart请求的最大容量（单位：字节），默认无限制
        // 参数4：fileSizeThreshold上传文件大小写入临时目录的临界值，默认0
        MultipartConfigElement element = new MultipartConfigElement("/tmp",
                2097152, 4194304, 0);
        // 通过Dynamic对象设置multipart配置
        registration.setMultipartConfig(element);
    }*/
}
