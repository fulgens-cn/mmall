package cn.fulgens.mmall.config;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * MyBatis配置类
 *
 * @author fulgens
 */
@Configuration
@EnableTransactionManagement
@PropertySource(value = {"classpath:mybatis.properties"})
public class MybatisConfig {

    /**
     * 配置SqlSessionFactoryBean
     * @param dataSource
     * @param pageInterceptor
     * @return
     */
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource, PageInterceptor pageInterceptor) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // 注入数据源
        sqlSessionFactoryBean.setDataSource(dataSource);
        // 设置MyBatis核心配置文件
        ClassPathResource resource = new ClassPathResource("mybatis/sqlMapConfig.xml");
        sqlSessionFactoryBean.setConfigLocation(resource);
        // 设置别名包
        sqlSessionFactoryBean.setTypeAliasesPackage("cn.fulgens.mmall.pojo");
        // 设置mapper文件位置
        Resource[] mapperResource = new Resource[0];
        try {
            mapperResource = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:mybatis/mappers/*.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlSessionFactoryBean.setMapperLocations(mapperResource);
        // 配置PageHelper插件
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageInterceptor});
        return sqlSessionFactoryBean;
    }

    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

    /**
     * 配置Mapper接口扫描
     * @return
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("cn.fulgens.mmall.mapper");
        return mapperScannerConfigurer;
    }

    /**
     * 配置事务管理器
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

}
