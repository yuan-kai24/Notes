# spring

深入理解spring

## 数据源

**切换：**

排除掉自动配置

```java
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        JdbcTemplateAutoConfiguration.class
})// 排除spring的自动配置
```

使用自己的配置

```java
@SpringBootConfiguration
/*** 多数据源配置 **/
@Slf4j
@MapperScan(basePackages  = {"com.study.dissect.dao"}, sqlSessionFactoryRef = "rootSqlSessionFactory",sqlSessionTemplateRef = "rootSqlSessionTemplate")
@MapperScan(basePackages  = {"com.study.dissect.dao2"}, sqlSessionFactoryRef = "userSqlSessionFactory",sqlSessionTemplateRef = "userSqlSessionTemplate")
public class TestDatasource {

    // 读取相应的配置
    @Bean
    @ConfigurationProperties("root.datasource")
    public DataSourceProperties rootDataSourceProperties() {
        log.info("root datasource:{}","成功读取配置" );
        return new DataSourceProperties();
    }

    // 使用该配置注册数据源
    @Bean
    public DataSource rootDataSource(){
        DataSourceProperties dataSourceProperties = rootDataSourceProperties();
        log.info("root datasource:{}",dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    // 为该数据源创建事务管理器
    @Bean
    @Resource
    public PlatformTransactionManager fooTxManager(DataSource rootDataSource){
        return new DataSourceTransactionManager(rootDataSource);
    }

    @Bean(name="rootSqlSessionFactory")
    public SqlSessionFactory rootSqlSessionFactory(@Qualifier("rootDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean=new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean(name="rootSqlSessionTemplate")
    public SqlSessionTemplate rootSqlSessionTemplate(@Qualifier("rootSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    /*** ===================================================================================== */
    @Bean
    @ConfigurationProperties("user.datasource")
    public DataSourceProperties userDataSourceProperties() {
        log.info("user.datasource:{}","成功读取配置" );
        return new DataSourceProperties();
    }

    @Bean
    public DataSource userDataSource(){
        DataSourceProperties dataSourceProperties = userDataSourceProperties();
        log.info("user datasource:{}",dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Resource
    public PlatformTransactionManager barTxManager(DataSource userDataSource){
        return new DataSourceTransactionManager(userDataSource);
    }

    @Bean(name="userSqlSessionFactory")
    public SqlSessionFactory userSqlSessionFactory(@Qualifier("userDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean=new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean(name="userSqlSessionTemplate")
    public SqlSessionTemplate userSqlSessionTemplate(@Qualifier("userSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

```

**连接池**

spring boot 2.x，默认使用HikariCP

配置项上：spring.datasource.hikari.*配置



spring boot 1.x，默认使用的是tomcat的连接池，需要移除tomcat-jdbc依赖

spring.datasource.type=com.zaxxer.hikari.HikariDataSource



**HikariCP**

做了很字节码级别的优化

如：

代理优化，使用了invokesstatic代替了invokevirtual：静态成员变量换成了静态方法由18字节到了15字节

使用FastStatementList代替ArrayList

无锁集合 ConcurrentBag



**Druid**

阿里巴巴的开源数据库连接池，为监控而生，内置强大的监控功能，监控特性不影响性能。能防止sql注入，内置logging能诊断Hack行为。

内置加密行为，历经双十一等考验，非常强大。

扩展点很多。



**选择考量点**

可扩展，可运维，可靠（如网络抖动自动恢复），性能，功能适用，社区是否活跃 

连接池扩展：

追踪sql语句，在数据库连接池上，每条sql前加注释id（事件:Events id，追踪Trace id），更通用，

## 对象查看

依赖

```xml
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
 </dependency>
```

配置

在builder里面配置

```xml
<executions>
    <execution>
        <goals>
            <goal>build-info</goal>
        </goals>
    </execution>
</executions>
```

