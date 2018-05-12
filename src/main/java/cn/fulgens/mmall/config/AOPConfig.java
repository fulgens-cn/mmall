package cn.fulgens.mmall.config;

import cn.fulgens.mmall.aspect.RequestAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy     // 启用AspectJ自动代理
public class AOPConfig {

    // 需将切面类配置进来
    @Bean
    public RequestAspect requestAspect() {
        return new RequestAspect();
    }

}
