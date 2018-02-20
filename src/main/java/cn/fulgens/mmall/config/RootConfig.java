package cn.fulgens.mmall.config;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = {"cn.fulgens.mmall"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class)
})
@Import({DataSourceConfig.class, MybatisConfig.class, RedisConfig.class, AliPayConfig.class})
@PropertySource(value = {"classpath:mmall.properties"})
public class RootConfig {

}
