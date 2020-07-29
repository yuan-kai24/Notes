# JAVA

Java随笔

## WebSocket

一种基于socket的技术，可以让浏览器和服务端保持联系从而达到即时信息传输的技术。

这是一种后来的协议(ws)，tomcat7之前不支持，后来才内置的。

采用异步通讯方式，用线程池来处理，可并发量非常乐观。

### 前端代码

```javascript
//创建
var socket = new WebSocket('ws://localhost:8080')
//打开
socket.onopen = function(event){
    // 发送初始消息
	socket.send('info')
    
    // 监听消息
    socket.onmessage = function(event){
        console.log('监听')
    }
    
    // 监听socket关闭
    socket.onclose = function(event){
        console.log('关闭')
    }
    
    // 关闭socket
    socket.close()
}
```

### 后端代码

以下是tomcat自带的，其他的也大同小异

```java
// 自带线程安全的map
private static Map<String, Session> sessionCache = new ConcurrentHashMap<String, Session>();

// 服务端链接
@OnOpen
public void onOpen(Session session) throws IOException {
    sessionCache.put(session.getId(), session);
    System.out.println("ID为" + session.getId() + "的客户端接入");
    RemoteEndpoint.Basic basic = session.getBasicRemote();
    basic.sendText("欢迎你");
    System.out.println("当前人数：" + sessionCache.size());
}

@OnMessage
public void onMessage(Session session, String msg) {
    System.out.println("收到消息：" + msg);
}

@OnClose
public void onClose(Session session) {
    sessionCache.remove(session.getId());
    System.out.println("ID为" + session.getId() + "的客户端断开");
}

@OnError
public void onError(Throwable throwable, Session session) {
    System.out.println("进入错误处理");
}
```

maven

```html
<dependency>
    <groupId>javax.websocket</groupId>
    <artifactId>javax.websocket-api</artifactId>
    <version>1.1</version>
    <scope>provided</scope>
</dependency>
```

## ab测试

```shell
ab -n 100 -c 10 http://test_url
# -n 表示请求数，-c 表示并发数. -t 表示多少s内并发和请求
```

## Spring

### 数据源

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

### 对象查看

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



## Spring Boot

### RestFul风格

相同的接口，不同的访问方式，执行不同的操作。传入的参数也因访问方式不同而不同。

#### Get

- 安全且幂等
- 获取表示
- 变更时获取表示（缓存）

- 200（OK） - 表示已在响应中发出

- 204（无内容） - 资源有空表示
- 301（Moved Permanently） - 资源的URI已被更新
- 303（See Other） - 其他（如，负载均衡）
- 304（not modified）- 资源未更改（缓存）
- 400 （bad request）- 指代坏请求（如，参数错误）
- 404 （not found）- 资源不存在
- 406 （not acceptable）- 服务端不支持所需表示
- 500 （internal server error）- 通用错误响应
- 503 （Service Unavailable）- 服务端当前无法处理请求

#### Post

- 不安全且不幂等
- 使用服务端管理的（自动产生）的实例号创建资源
- 创建子资源
- 部分更新资源
- 如果没有被修改，则不过更新资源（乐观锁）

- 200（OK）- 如果现有资源已被更改

- 201（created）- 如果新资源被创建
- 202（accepted）- 已接受处理请求但尚未完成（异步处理）
- 301（Moved Permanently）- 资源的URI被更新
- 303（See Other）- 其他（如，负载均衡）
- 400（bad request）- 指代坏请求
- 404 （not found）- 资源不存在
- 406 （not acceptable）- 服务端不支持所需表示
- 409 （conflict）- 通用冲突
- 412 （Precondition Failed）- 前置条件失败（如执行条件更新时的冲突）
- 415 （unsupported media type）- 接受到的表示不受支持
- 500 （internal server error）- 通用错误响应
- 503 （Service Unavailable）- 服务当前无法处理请求

#### Put

- 不安全但幂等
- 用客户端管理的实例号创建一个资源
- 通过替换的方式更新资源
- 如果未被修改，则更新资源（乐观锁）

- 200 （OK）- 如果已存在资源被更改

- 201 （created）- 如果新资源被创建
- 301（Moved Permanently）- 资源的URI已更改
- 303 （See Other）- 其他（如，负载均衡）
- 400 （bad request）- 指代坏请求
- 404 （not found）- 资源不存在
- 406 （not acceptable）- 服务端不支持所需表示
- 409 （conflict）- 通用冲突
- 412 （Precondition Failed）- 前置条件失败（如执行条件更新时的冲突）
- 415 （unsupported media type）- 接受到的表示不受支持
- 500 （internal server error）- 通用错误响应
- 503 （Service Unavailable）- 服务当前无法处理请求

#### Delet

- 不安全但幂等
- 删除资源

- 200 （OK）- 资源已被删除

- 301 （Moved Permanently）- 资源的URI已更改
- 303 （See Other）- 其他，如负载均衡
- 400 （bad request）- 指代坏请求
- 404 （not found）- 资源不存在
- 409 （conflict）- 通用冲突
- 500 （internal server error）- 通用错误响应
- 503 （Service Unavailable）- 服务端当前无法处理请求

### 验证

用于校验前端传往后端数据

#### 流程

@NotBlank写在javabean的属性上，判断是否为空

@Vaild写在形参前，验证传入数据

BindingResult，作为形参一个参数，记录错误信息，使用后方法可以执行

一般配合post与@RequestBody使用

#### 常用验证注解

写在javabean的字段上,配合@Valid做数据校验

##### @NotBlank

值不能为空

##### @Null

值必须为空

##### @Pattern(regex=)

字符串必须匹配正则表达式

##### @Size(min=, max=)

集合的元素必须在min到max之间

##### @CreditCardNumber(ignoreNonDigitCharacters=)

字符串必须是信用卡号

按美国的校验标准，没啥*用

##### @Email

字符串必须是邮箱

##### @Length(min=, max=)

检查字符串长度

##### @NotEmpty

字符串不为null，集合有元素

##### @Range(min=, max=)

数字必须大于等于min，小于等于max

##### @SafeHtml

字符串必须是安全的html

##### @URL

字符串是合法的URL

##### @AssertFalse

值必须是false

##### @AssertTrue

值必须是true

##### @DecimalMax(value=, includsive=)

includsive:值为true条件为小于等于，false为小于

值必须小于等于value

可以在字符串类型的属性上使用

##### @DecimalMin(value=, includsive=)

includsive:值为true条件为大于等于，false为大于

值必须大于等于value

可以在字符串类型的属性上使用

##### @Digits(integer=, fraction=)

数字格式检查，integer指定整数部分最大长度，fraction指定小数部分的最大长度

##### @Future

值必须为未来日期

##### @Past

值必须是过去日期

##### @Max(value=)

值必须小于等于value指定的值，不能用在字符串类型的属性上

##### @Min(value=)

值必须大于等于value指定的值，不能用在字符串类型的属性上

#### 自定义注解

```java
/** 
    自定义校验注解 使用时只需要@MyConstraint(message="")即可
    因为message没有默认值，所以需要手动指定，此属性值用于抛出错误时返回的信息
    返回的信息在BindingResult类中取，详情看如下方法，省略方法声明
    (@Valid @RequestBody User user, BindingResult errors){
        if (errors.hasErrors()){
            errors.getAllErrors().stream().forEach(error -> {
                System.out.println(error.getDefaultMessage());
            });
        }
     }
*/
// 声明在方法与字段上
@Target({ElementType.METHOD, ElementType.FIELD})
// 运行时注解
@Retention(RetentionPolicy.RUNTIME)
// 指定处理类
@Constraint(validatedBy = MyConstraintValidator.class)
public @interface MyConstraint {
    String message() ;
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
```



```java
/**
	spring读取到ConstraintValidator接口时会自动注入，所以不用声明为组件
	< , >第一个为注解类，第二个为处理的数据类型
	initialize： 初始化执行方法
	isValid： 校验方法，value为传过来的值，对应第二个泛型指定的类型
			  false为失败，自动装入BindingResult中，true为成功
*/
public class MyConstraintValidator implements ConstraintValidator<MyConstraint, Object> {

    @Override
    public void initialize(MyConstraint constraintAnnotation) { }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return false;
    }
}
```



### 返回

定制后端返回给前端的数据

#### @JsonView

在javabean内创建相应的interface。

在相应属性的get方法上使用该注解@JsonView(XXXX.class)

可以通过继承的方式，继承另外interface的属性，多继承少原则

需要使用时，在相应的路由方法上使用@JsonView即可使用：

​			@JsonView(JavaBean.Interface.class)

### 错误处理机制

Spring Boot默认使用路由注解中produce属性区分text/html（浏览器访问）与其他方式访问，浏览器方式访问返回错误页面。其他方式访问返回错误接口。

#### 定制

##### 页面定制

在资源路径resources下，增加resources/error文件夹，文件夹名为状态码的HTML文件实现页面定制

##### 接口定制

一般来说，程序抛出异常，就会进入错误处理机制，返回的错误接口中，会附带一些错误信息在error里。

自己定义的异常中，可以设置和Javabean一样的字段，异常抛出时，传进去，spring会自动读取，在路由方法上配置@ExceptionHandler(异常类名.class)后按照自定义的异常类里面的javabean返回错误信息。

### 过滤器

java.servlet.Filter

声明为spring组件即可自动注入

init:：初始化

destroy：销毁

doFilter：处理过滤逻辑

##### 第三方过滤器

有些第三方类只是一个普通的类，在没了xml配置的spring boot项目中，可以使用如下方式解决：

```java
@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean timeFilter(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        TestFilter testFilter = new TestFilter();
        registrationBean.setFilter(testFilter);

        // 指定在哪些URL时起作用
        List<String> urls = new ArrayList<>();
        urls.add("/*");// 所有路径都起作用
        registrationBean.setUrlPatterns(urls);
        return registrationBean;
    }
}
```



### 拦截器

spring的拦截器，相比较于传统的过滤器，虽然优先级在其之下，不止能拿到request与response，还能知道在哪个方法，哪个类里执行的，因为Filter是J2EE规范的，并没有spring相关信息。

HandlerInterceptor

preHandler：调用之前访问的方法，返回false代表拦截

postHandler：调用之后访问的方法，调用的方法抛出异常不会被调用

afterCompletion：在调用之后始终会被调用，多用于处理异常

配置：

```java
@Configuration
public class WebConfig extends WebMvcSecurityConfiguration {

    @Resource
    private TimeInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
}
```



### 切片