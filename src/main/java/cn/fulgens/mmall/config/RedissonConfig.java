package cn.fulgens.mmall.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;
import java.io.IOException;

/**
 * Redisson配置类
 *
 * @author fulgens
 */
@Configuration
@PropertySource(value = {"classpath:redis.properties"}, encoding = "UTF-8")
public class RedissonConfig {

    @Bean(destroyMethod="shutdown")
    RedissonClient redissonClient() throws IOException {
        // 单机配置
        // Config config = Config.fromYAML(getClass().getResourceAsStream("redisson-single-server-config.yaml"));

        // 哨兵配置
        Config config = Config.fromYAML(new File(getClass().getClassLoader().getResource("redisson-sentinel-config.yaml").getFile()));

        // 集群配置
        // Config config = Config.fromYAML(getClass().getResourceAsStream("redisson-cluster-config.yaml"));
        return Redisson.create(config);
    }
}
