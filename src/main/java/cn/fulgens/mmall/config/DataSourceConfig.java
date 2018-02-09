package cn.fulgens.mmall.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = {"classpath:jdbc.properties"})
// 相当于<context:property-placeholder location="classpath:jdbc.properties"/>
public class DataSourceConfig {

    @Autowired
    private Environment env;

    // 配置Druid连接池
    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        // 参考https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE_DruidDataSource%E5%8F%82%E8%80%83%E9%85%8D%E7%BD%AE
        // 基本属性配置
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.username"));
        dataSource.setPassword(env.getProperty("jdbc.password"));
        // 配置初始化连接数
        dataSource.setInitialSize(Integer.valueOf(env.getProperty("jdbc.initialSize")));
        // 配置最小空闲连接
        dataSource.setMinIdle(Integer.valueOf(env.getProperty("jdbc.minIdle")));
        // 配置最大连接数量
        dataSource.setMaxActive(Integer.valueOf(env.getProperty("jdbc.maxActive")));
        // 配置获取连接超时等待的时间
        dataSource.setMaxWait(Long.valueOf(env.getProperty("jdbc.maxWait")));
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接
        dataSource.setTimeBetweenEvictionRunsMillis(Long.valueOf(env.getProperty("jdbc.timeBetweenEvictionRunsMillis")));
        // 配置一个连接在池中最小生存的时间
        dataSource.setMinEvictableIdleTimeMillis(Long.valueOf(env.getProperty("jdbc.minEvictableIdleTimeMillis")));
        return dataSource;
    }

    @Bean
    // 配置jdbc模板
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
