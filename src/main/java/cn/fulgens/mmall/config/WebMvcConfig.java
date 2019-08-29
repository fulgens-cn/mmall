package cn.fulgens.mmall.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * SpringMVC配置类
 *
 * @author fulgens
 */
@Configuration
@EnableWebMvc
@ComponentScan(
        basePackages = {"cn.fulgens.mmall.controller"},
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)
        }
)
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public ITemplateResolver templateResolver() {
        // Thymeleaf3使用ITemplateResolver接口，SpringResourceTemplateResolver实现类
        // Thymeleaf3之前使用TemplateResolver接口，ServletContextTemplateResolver实现类
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        // 设置templateMode属性为HTML5
        templateResolver.setTemplateMode("HTML5");
        // 设置编码格式为UTF-8
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);
        // 开发时设为false
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    // email模板使用的解析器
    /*@Bean
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("mail/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(1);
        return resolver;
    }*/

    @Bean
    public TemplateEngine templateEngine(Set<ITemplateResolver> resolvers) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        // 注入模板解析器
        templateEngine.setTemplateResolvers(resolvers);
        return templateEngine;
    }

    @Bean
    @Primary
    public ViewResolver viewResolver(TemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");
        return viewResolver;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // 放行静态资源
        // 相当于xml配置中的<mvc:default-servlet-handler/>
        configurer.enable();
    }

    /*@Bean
    // 国际化信息源ResourceBundleMessageSource
    // 需配合spring标签<s:message code="..." />使用
    // 会在类路径下查找信息源配置文件
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // 设置国际化信息源的基本名称
        messageSource.setBasename("messages");
        return messageSource;
    }*/

    // 国际化信息源ReloadableResourceBundleMessageSource
    // 不同于ResourceBundleMessageSource它能够重新加载信息属性，而不必重新编译或重启应用
    // baseName属性可以设置在类路径下（以"classpath:"作为前缀）、文件系统中（以"file:"作为前缀）
    // 或web应用的根路径下（没有前缀）
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        // 设置国际化信息源的基本名称
        messageSource.setBasename("classpath:messages");
        messageSource.setCacheSeconds(10);
        return messageSource;
    }

    // 文件上传解析器
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        // 最大上传文件大小，单位字节，这里设置为2M（2*1024*1024）
        multipartResolver.setMaxUploadSize(2097152);
        // 文件上传过程中内存存储大小
        multipartResolver.setMaxInMemorySize(0);
        // 设置默认编码为UTF-8
        multipartResolver.setDefaultEncoding("UTF-8");
        return multipartResolver;
    }

    // 重写configureMessageConverters方法使用fastjson作为json数据的消息转换器
    // Fastjson 版本小于1.2.36，在与Spring MVC 4.X 版本集成时需使用 FastJsonHttpMessageConverter4
    // 参考：https://github.com/alibaba/fastjson/wiki/%E5%9C%A8-Spring-%E4%B8%AD%E9%9B%86%E6%88%90-Fastjson
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);

        // 创建fastjson提供的消息转换器FastJsonHttpMessageConverter
        FastJsonHttpMessageConverter messageConverter = new FastJsonHttpMessageConverter();
        // 创建fastjson配置对象FastJsonConfig
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 设置格式化json数据
        // fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        /* WriteNullNumberAsZero—-数值字段如果为null,输出为0,而非null
         WriteNullListAsEmpty—–List字段如果为null,输出为[],而非null
         WriteNullStringAsEmpty—字符类型字段如果为null,输出为"",而非null
         WriteNullBooleanAsFalse–Boolean字段如果为null,输出为false,而非null*/
        // 设置输出值为null的字段,默认为false
        // fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
        // 为消息转换器设置配置
        messageConverter.setFastJsonConfig(fastJsonConfig);
        // 配置支持的MediaType
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON_UTF8);
        messageConverter.setSupportedMediaTypes(mediaTypeList);
        // 添加fastjson消息转换器到转换器列表中
        converters.add(messageConverter);
    }

}
