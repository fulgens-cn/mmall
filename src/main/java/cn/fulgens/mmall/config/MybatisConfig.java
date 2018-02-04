package cn.fulgens.mmall.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement    // 启用事务管理器
public class MybatisConfig {

    // 配置SqlSessionFactoryBean
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // 注入数据源
        sqlSessionFactoryBean.setDataSource(dataSource);
        // 设置MyBatis核心配置文件
        /*ClassPathResource resource = new ClassPathResource("mybatis/sqlMapConfig.xml");
        sqlSessionFactoryBean.setConfigLocation(resource);*/
        // 设置别名包
        sqlSessionFactoryBean.setTypeAliasesPackage("cn.fulgens.mmall.pojo");
        return sqlSessionFactoryBean;
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
