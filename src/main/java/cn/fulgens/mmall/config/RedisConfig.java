package cn.fulgens.mmall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis配置类
 *
 * @author fulgens
 */
@Configuration
@PropertySource(value = {"classpath:redis.properties"}, encoding = "UTF-8")
public class RedisConfig {

    @Autowired
    private Environment env;

    /**
     * Jedis连接池配置
     * @return
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.parseInt(env.getProperty("redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxIdle(Integer.parseInt(env.getProperty("redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.parseInt(env.getProperty("redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(env.getProperty("redis.jedis.pool.max-wait")));
        return jedisPoolConfig;
    }

    /**************************单机配置 start***************************/

    /*@Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig poolConfig) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName(env.getProperty("redis.host"));
        connectionFactory.setPort(Integer.parseInt(env.getProperty("redis.port")));
        connectionFactory.setPassword(env.getProperty("redis.password"));
        connectionFactory.setUseSsl(Boolean.parseBoolean(env.getProperty("redis.ssl")));
        connectionFactory.setTimeout(Integer.parseInt(env.getProperty("redis.timeout")));
        connectionFactory.setDatabase(Integer.parseInt(env.getProperty("redis.database")));
        connectionFactory.setPoolConfig(poolConfig);
        return connectionFactory;
    }*/

    /**************************单机配置 end***************************/

    /**************************哨兵配置 start***************************/

    /**
     * 哨兵配置
     *
     * @return
     */
    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration();

        sentinelConfiguration.setMaster(env.getProperty("redis.sentinel.master"));
        Set<String> nodes = StringUtils.commaDelimitedListToSet(env.getProperty("redis.sentinel.nodes"));
        Set<RedisNode> sentinelNodes = nodes.stream().map(node -> {
            String[] hostAndPort = StringUtils.split(node, ":");
            return new RedisNode(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
        }).collect(Collectors.toSet());
        sentinelConfiguration.setSentinels(sentinelNodes);
        return sentinelConfiguration;
    }

    /**
     * 哨兵连接工厂
     * @param sentinelConfiguration
     * @param poolConfig
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisSentinelConfiguration sentinelConfiguration,
                                                         JedisPoolConfig poolConfig) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(sentinelConfiguration, poolConfig);
        // connectionFactory.setPassword(env.getProperty("redis.password"));
        connectionFactory.setUseSsl(Boolean.parseBoolean(env.getProperty("redis.ssl")));
        connectionFactory.setTimeout(Integer.parseInt(env.getProperty("redis.timeout")));
        connectionFactory.setDatabase(Integer.parseInt(env.getProperty("redis.database")));
        connectionFactory.setPoolConfig(poolConfig);
        return connectionFactory;
    }

    /**************************哨兵配置 end***************************/

    /**************************集群配置 start***************************/

    /*@Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        clusterConfiguration.setMaxRedirects(Integer.parseInt(env.getProperty("redis.cluster.max-redirects")));
        Set<String> nodes = StringUtils.commaDelimitedListToSet(env.getProperty("redis.cluster.nodes"));
        Set<RedisNode> clusterNodes = nodes.stream().map(node -> {
            String[] hostAndPort = StringUtils.split(node, ":");
            return new RedisClusterNode(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
        }).collect(Collectors.toSet());
        clusterConfiguration.setClusterNodes(clusterNodes);
        return clusterConfiguration;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisClusterConfiguration clusterConfiguration,
                                                         JedisPoolConfig poolConfig) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(clusterConfiguration, poolConfig);
        return jedisConnectionFactory;
    }*/

    /**************************集群配置 end***************************/

    /**
     * RedisTemplate配置
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(connectionFactory);

        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * StringRedisTemplate配置
     * @param connectionFactory
     * @return
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);
        return stringRedisTemplate;
    }

}
