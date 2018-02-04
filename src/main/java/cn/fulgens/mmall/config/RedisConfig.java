package cn.fulgens.mmall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableCaching      // 启用spring对缓存的支持
public class RedisConfig {

    @Autowired
    private Environment env;

    // 配置缓存管理器
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        return new RedisCacheManager(redisTemplate);
    }

    // 配置redis链接工厂
    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig poolConfig) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("127.0.0.1");
        jedisConnectionFactory.setPort(6379);
        jedisConnectionFactory.setPoolConfig(poolConfig);
        return jedisConnectionFactory;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10000);
        jedisPoolConfig.setMaxIdle(20);
        jedisPoolConfig.setMinIdle(5);
        jedisPoolConfig.setMaxWaitMillis(5000);
        return jedisPoolConfig;
    }

    // 配置redis模板
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    /**************************集群配置***************************/
    // 配置redis连接工厂
    /*@Bean
    public RedisConnectionFactory redisConnectionFactory(
            RedisClusterConfiguration clusterConfiguration,
            JedisPoolConfig poolConfig) {
        JedisConnectionFactory jedisConnectionFactory =
                new JedisConnectionFactory(clusterConfiguration, poolConfig);
        return jedisConnectionFactory;
    }

    // 配置Jedis连接池
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大连接数
        jedisPoolConfig.setMaxTotal(Integer.valueOf(env.getProperty("")));
        // 最大空闲时间
        jedisPoolConfig.setMaxIdle(Integer.valueOf(env.getProperty("")));
        // 最大等待时间
        jedisPoolConfig.setMaxWaitMillis(Long.valueOf(env.getProperty("")));
        return jedisPoolConfig;
    }

    // Redis集群配置
    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        RedisClusterConfiguration redisClusterConfiguration =
                new RedisClusterConfiguration();
        redisClusterConfiguration.setMaxRedirects(3);
        Set<RedisNode> nodes = new HashSet<>();
        nodes.add(new RedisClusterNode("", 6379));
        nodes.add(new RedisClusterNode("", 6379));
        nodes.add(new RedisClusterNode("", 6379));
        nodes.add(new RedisClusterNode("", 6379));
        nodes.add(new RedisClusterNode("", 6379));
        nodes.add(new RedisClusterNode("", 6379));
        redisClusterConfiguration.setClusterNodes(nodes);
        return redisClusterConfiguration;
    }*/

}
