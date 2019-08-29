package cn.fulgens.mmall.config;

import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(
        basePackages = {"cn.fulgens.mmall"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)
        }
)
@Import({
        DataSourceConfig.class,
        MybatisConfig.class,
        RedisConfig.class,
        AliPayConfig.class,
        AopConfig.class,
        SessionConfig.class
})
@PropertySource(value = {"classpath:mmall.properties"})
public class RootConfig {

}
