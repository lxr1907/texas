package yuelj;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@MapperScan(basePackages = "yuelj.dao.impl", sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterDataSourceConfig {
	// 精确到 master 目录，以便跟其他数据源隔离
    static final String MAPPER_LOCATION = "classpath:mapper/*.xml";
    static final String TYPE_ALIAS_PACKAGE = "yuelj.entity";
 
	 /**
     * 创建test1的DataSource实例
     * @return
     */
    @Primary //配置一个主连接
    @Bean(name = "masterDataSource")
    @Qualifier("masterDataSource")
    @ConfigurationProperties(prefix="texas.spring.datasource")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(MasterDataSourceConfig.MAPPER_LOCATION));
        sessionFactory.setTypeAliasesPackage(TYPE_ALIAS_PACKAGE);
        return sessionFactory.getObject();
    }
}
