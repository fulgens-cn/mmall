package cn.fulgens.mmall.config;

import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement    // 启用事务管理器
// @PropertySource(value = {"classpath:mybatis.properties"})
public class MybatisConfig {

    @Autowired
    private Environment env;

    // 配置SqlSessionFactoryBean
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource,
                                                       PageHelper pageHelper) {
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
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
        return sqlSessionFactoryBean;
    }

    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        // PageHelper配置参见https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/en/HowToUse.md
        return pageHelper;
    }

    // 配置Mapper接口扫描
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer =
                new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("cn.fulgens.mmall.dao");
        return mapperScannerConfigurer;
    }

    // 配置事务管理器
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager =
                new DataSourceTransactionManager();
        // 注入数据源
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

}
