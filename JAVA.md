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

## 常用特性

### 异常

try，在1.7以后，可以在括号内声明一些流，执行完毕后自动关闭，不用手动关了。

```java
try(
                InputStream inputStream = new FileInputStream(new File(path ,id));
                OutputStream out = response.getOutputStream();
                ) {
            System.out.println("代码区");
        }
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

相比较拦截器，能拿到传入参数的值，但不能拿到响应的http请求响应信息

@Aspect // 声明是切面类

@Before // 方法调用之前

@After // 方法调用之后

@AfterThrowing // 方法抛出异常

@Around // 在什么时候什么方法上起作用（常用）

```java
@Aspect
@Component
public class TimeAspect {
    /**
        execution: 执行
        com.yk.web.controller.UserController： 要拦截的类
        .*(..)：任何方法，包含任何参数
        第一个* ： 任何返回值
        ProceedingJoinPoint：传入方法的参数信息，返回信息（pjp.proceed()）等
        					 最终前端拿到的，是这个方法返回的值
    */
    @Around("execution(* com.yk.web.controller.UserController.*(..))")
    public Object before(ProceedingJoinPoint pjp) throws Throwable {
        return null;
    }
}
```

### 上传下载

```java
@RestController
public class FileController {
    String path = "F:\\IDEA_Java\\yk-security\\yk-security-demo\\src\\main\\java\\com\\yk\\web\\controller";
    @PostMapping("/file")
    public FileInfo upload(@RequestParam MockMultipartFile file) throws IOException {
        FileInfo fileInfo = new FileInfo();// 返回的bean
        String fileName = new Date().getTime() + ".txt";
        fileInfo.setPath(fileName);
        File localFile = new File(path, fileName);// 标明路径与名称
        file.transferTo(localFile);// 写入
        return fileInfo;
    }

    @GetMapping("/file/{id}")
    public void download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try(
                InputStream in = new FileInputStream(new File(path ,id));
                OutputStream out = response.getOutputStream();
                ) {
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=test.txt");
            IOUtils.copy(in, out);
        }
    }
}
```

### 异步处理RestFul

在同步处理的情况下，http请求进来，tomcat有固定的线程数处理响应，如果请求过多，会造成大量阻塞。

在异步的情况下，当请求到达主线程，就调用副线程处理，把主线程空闲出来处理其他请求，提高吞吐量。

注：异步情况最终返回是主线程返回，处理过程交于副线程而已。

消息队列线程隔离处理：

- 收到请求
- 发处理数据到消息到队列
- 监听处理消息
- 发送处理结果到消息队列
- 监听处理结果
- 响应处理结果

#### Callable

这种方式虽然可以实现，但线程依然是由主线程调起的，异步的不彻底，同样不适合异步消息队列处理，但性能与吞吐量有一定提升。

```java
@RestController
public class AsyncController {

    private final Logger logger = LoggerFactory.getLogger(AsyncController.class);

    @RequestMapping("/sync")
    public String sync() throws Exception {
        logger.info("主线程开始...");
        Thread.sleep(1000);
        logger.info("主线程返回...");
        return "order1 success";
    }
    @RequestMapping("/async")
    public Callable<String> async() throws Exception {
        logger.info("主线程开始...");
        Callable<String> result = new Callable<String>() {
            @Override
            public String call() throws Exception {
                logger.info("副线程开始...");
                Thread.sleep(1000);
                logger.info("副线程返回...");
                return "order2 success";
            }
        };
        logger.info("主线程返回...");
        return result;
    }
}
```

#### 模拟消息队列

使用了三个线程，线程之间相互隔离，采用MockQueue模拟消息处理，DeferredResultHoler返回结果。

```java
@RestController
public class AsyncController {

    @Resource
    private MockQueue mockQueue;

    @Resource
    private DeferredResultHolder deferredResultHolder;

    private final Logger logger = LoggerFactory.getLogger(AsyncController.class);

    @RequestMapping("/sync")
    public String sync() throws Exception {
        logger.info("主线程开始...");
        Thread.sleep(1000);
        logger.info("主线程返回...");
        return "order1 success";
    }
    @RequestMapping("/async")
    public DeferredResult<String> async() {
        logger.info("主线程开始...");
        String info = RandomStringUtils.randomNumeric(8);
        mockQueue.setPlaceInfo(info);
        DeferredResult<String> result = new DeferredResult<>();
        deferredResultHolder.getMap().put(info, result);
        logger.info("主线程返回...");
        return result;
    }
}
```

监听类

ContextRefreshedEvent: spring初始化完毕的一个事件

```java
@Component
public class QueueListener implements ApplicationListener<ContextRefreshedEvent> {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private MockQueue mockQueue;
    @Resource
    private DeferredResultHolder deferredResultHolder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        new Thread(() -> {
            while (true) {
                if (StringUtils.isNotBlank(mockQueue.getCompleteInfo())) {
                    String completeInfo = mockQueue.getCompleteInfo();
                    logger.info("监听到消息->返回处理结果：" + completeInfo);
                    deferredResultHolder.getMap().get(completeInfo).setResult("place info success");
                    mockQueue.setCompleteInfo(null);
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
```

模拟消息队列处理

```java
@Component
public class MockQueue {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 需要处理的消息 */
    private String placeInfo;

    /** 需要响应的消息 */
    private String completeInfo;

    public String getPlaceInfo() {
        return placeInfo;
    }

    public void setPlaceInfo(String placeInfo) {

        new Thread(() -> {
            logger.info("接到需要处理的消息： " + placeInfo);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.completeInfo = placeInfo;
            logger.info("消息： " + placeInfo + " [处理完毕]");
        }).start();

    }

    public String getCompleteInfo() {
        return completeInfo;
    }

    public void setCompleteInfo(String completeInfo) {
        this.completeInfo = completeInfo;
    }
}
```

DeferredResult作用于线程之间通讯

```java
@Component
public class DeferredResultHolder {
    private final Map<String, DeferredResult<String>> map = new HashMap<>();

    public Map<String, DeferredResult<String>> getMap() {
        return map;
    }
}
```

#### 异步配置

```java
@Configuration
public class WebConfig extends WebMvcSecurityConfiguration {
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 拦截器配置
        configurer.registerCallableInterceptors(null); // callable方式所需
        configurer.registerDeferredResultInterceptors(null);// deferred方式所需
    }
}
```

### 文档与服务伪造

#### Swagger

自动生成HTML文档

依赖：核心组件与UI

```xml
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.7.0</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.7.0</version>
        </dependency>
```

在入口处增加@EnableSwagger2

启动项目后访问/swagger-ui.html即可。

@ApiParam:  形参描述

@ApiOperation:  接口/方法 描述

@ApiModelProperty:  Javabean字段描述


#### WireMock

快速伪造RestFul服务

启动jar包： java -jar D:\wiremock\wiremock-standalone-2.27.0.jar --port 端口

依赖：

```xml
		<dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpmime</artifactId>
        </dependency>

        <dependency>
            <groupId>com.github.tomakehurst</groupId>
            <artifactId>wiremock</artifactId>
        </dependency>
```

编写服务

代码：

```java
public class MockServer {
    public static void main(String[] args) throws IOException {
        WireMock.configureFor(8082);
        // 清空服务
        WireMock.removeAllMappings();
        mock("/sync", "/mock/response/01.json");
    }

    public static void mock(String url, String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);
        String content = FileUtils.readFileToString(resource.getFile(),"UTF-8");
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo(url))
                .willReturn(WireMock.aResponse()
                        .withBody(content)
                        .withStatus(200)));
    }
}
```

## Spring Security

- 认证
- 授权
- 攻击防护

### 原理

由一个过滤器链做安全校验，默认为HttpBasic提交

### 配置

```properties
# 是否启用
security.basic.enabled=false
```

```java
/*
WebSecurityConfigurerAdapter:spring security提供的适配器配置类
configure(HttpSecurity http):不能有super.configure(http);，否则配置会失效
*/
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // super.configure(http);
    }
}
```

### 表单提交

配置为表单提交：

```java
http.httpBasic() // 默认方式
http.formLogin()
        .and()
        .authorizeRequests() // 标明为授权配置
        .anyRequest() // 任何请求
        .authenticated(); // 需要认证
```

#### 自定义认证

一般使用会从数据库取数据校验，下列只是简单校验

```java
@Component
public class MyUserDetailsService implements UserDetailsService {
    Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查找用户信息
        logger.info("用户：" + username + "<<<正在登陆");
        // 用户名，密码，权限
        User admin = new User(username, "123456"
                , AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        System.out.println(admin);
        // 用户名，密码，是否可用，账户是否没过期，密码是否没过期，是否没被锁定
        new User(username, "123456"
                ,true, true,true,true
                , AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        return admin;

    }
}
```

#### UserDetails

实现此接口，可以校验用户名，密码，可用，是否过期，是否锁定等等。

一旦配置PasswordEncoder后，传入的密码必须使用加密串。

#### 加密

加密接口：

org.springframework.security.crypto.password.PasswordEncoder

spring security的一个实现类

org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

​		这个类加密时，会随机生成一个salt，混在加密串里，最后再反推比较密码，所以相同的密码，每次生成的加密串都不同，但都可用。

```java
// 在config里配置一下
@Bean
public PasswordEncoder passwordEncoder(){
    return new  BCryptPasswordEncoder();
}
```

#### 个性化认证

自定义配置：

```java
// 在配置方法里使用,antMatchers必须紧跟authorizeRequests，后面的permitAll也一样
        http.formLogin()
                .loginPage("/signIn.html")
                .and()
                .authorizeRequests() // 标明为授权配置
                .antMatchers("/signIn.html").permitAll() // 不做认证
// 常用表单配置
        http.formLogin()
//                .loginPage("/signIn.html")
                .loginPage("/authentication/require") // 配置到自己的controller中
                .loginProcessingUrl("/authentication/form") // 配置请求路由
                .and()
                .csrf().disable()// 关闭跨站防护
                .authorizeRequests() // 标明为授权配置
                .antMatchers("/authentication/require",
                        securityProperties.getBrowser().getLoginPage()).permitAll() // 不做认证
                .anyRequest() // 任何请求
                .authenticated(); // 需要认证
```

自动化配置：

注入时尽量使用@AutoWrite

配置读取

```java
@ConfigurationProperties(prefix = "yk.security") // 读取配置文件中所有以yk.security开头的配置
public class SecurityProperties {

    private BrowserProperties browser = new BrowserProperties();

    public BrowserProperties getBrowser() {
        return browser;
    }

    public void setBrowser(BrowserProperties browser) {
        this.browser = browser;
    }
}
```

配置信息：

```java
public class BrowserProperties {
    private String loginPage = "/demo-signIn.html";

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }
}
```

使配置生效：

```java
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)// 使SecurityProperties配置生效
public class SecurityCoreConfig {
}
```

controller：

```java
@Autowired
private SecurityProperties securityProperties;
/**
 * 当需要身份认证时跳转到这里
 * @param request 请求
 * @param response 响应
 * @return string
 */
@RequestMapping("/authentication/require")
@ResponseStatus(code = HttpStatus.UNAUTHORIZED) // 返回 401 未授权状态码
public SimpleResponse requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Logger logger = LoggerFactory.getLogger(BrowserSecurityController.class);
    // 拿到请求信息
    SavedRequest save = requestCache.getRequest(request, response);
    if (save != null){
        String target = save.getRedirectUrl(); // 获取请求的接口
        logger.info("引发跳转的请求是：" + target);
        if (StringUtils.endsWithIgnoreCase(target, ".html")){ // 判断请求是接口还是页面
            redirectStrategy.sendRedirect(request, response, securityProperties.getBrowser().getLoginPage());
        }
    }
    return new SimpleResponse("访问的服务需要认证");// 返回给前端的json
}
```

##### ajax与跳转

配置登录成功与失败的类，然后再config里设置好，最后配置application

代码中的两种实现均是用的继承实现类，为实现配置化使用的判断

成功：

​		AuthenticationSuccessHandler： 接口，实现onAuthenticationSuccess为调用成功的方法

​		SavedRequestAwareAuthenticationSuccessHandler： spring 提供的默认类，默认为页面跳转

```java
@Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
        , Authentication authentication) throws IOException, ServletException {
    if (securityProperties.getBrowser().getLoginType().equals(LoginType.JSON)){
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(authentication));
    }else{
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
```

失败：

​		AuthenticationFailureHandler： 接口

​		SimpleUrlAuthenticationFailureHandler： 默认实现类

```java
@Override
public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response
        , AuthenticationException exception) throws IOException, ServletException {
    if (securityProperties.getBrowser().getLoginType().equals(LoginType.JSON)){
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(exception));
    }else{
        super.onAuthenticationFailure(request, response, exception);
    }
}
```

##### 验证码

以增量的方式适应变化，处理的方式不是改代码，而是新增的代码来自动替换代码。

以下代码和原理按照上面的标准来。

###### 图片验证码

封装一个图片验证码bean（ImageCode）： BufferedImage， 验证码， 过期时间

​		优化： 增加使用int设置过期时间的构造函数

建立验证码生成接口ValidateCodeGenerator，里面包含ImageCode generate(ServletWebRequest request)方法，生成后存入ImageCode返回回去。

建立图片验证码类，实现验证码生成接口，包含securityProperties配置类（生成getset，在后面配置里传入！！！此处也可以自动注入）。

建立配置类：

```java
@Configuration
public class ValidateCodeBeanConfig {

    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    @ConditionalOnMissingBean(name = "imageCodeGenerator") // 寻找容器内是否已经有这个bean了，如果有，就不用再使用这个bean了
    public ValidateCodeGenerator imageCodeGenerator(){
        ImageCodeGenerator codeGenerator= new ImageCodeGenerator();
        codeGenerator.setSecurityProperties(securityProperties);// 传入配置
        return codeGenerator;
    }
}
```

以上是图片验证码生成流程，别人有新的验证码生成时，只需要@Component("imageCodeGenerator")并且实现ValidateCodeGenerator就可以实现自动覆盖了。

**验证码认证流程**

建立controller

```java
public static final String SESSION_KEY = "SESSION_KEY_IMAGE_CODE";
private final SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
@Resource(name = "imageCodeGenerator")
private ValidateCodeGenerator imageCodeGenerator;
@GetMapping("/code/image")
public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ImageCode code = imageCodeGenerator.generate(new ServletWebRequest(request));
    sessionStrategy.setAttribute(new ServletRequestAttributes(request), SESSION_KEY, code); // 存储code，方便判断
    ImageIO.write(code.getImage(), "JPEG", response.getOutputStream());// 写入前端

}
```

建立配置信息类（类似于个性化认证）

```java
@Get@Set
public class ImageCodeProperties {
    /** 验证码宽度 */
    private int width = 67;
    /** 验证码高度 */
    private int height = 23;
    /** 验证码位数 */
    private int length = 4;
    /** 失效时间 */
    private int expireIn = 60;
    /** 请求url，有多个配置则用逗号隔开:/user,/user/* */
    private String url = "/code/image"
}
```

在SecurityProperties中配置，并实现getset：

```java
/** 验证配置 */
private ValidateCodeProperties code = new ValidateCodeProperties();
/*
此时可以在application中配置信息了，例如：
yk.security.code.image.length=6
yk.security.code.image.width=100
yk.security.code.image.url=/user,/user/*
*/
```

建立验证码验证过滤器public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean

- 继承spring提供的过滤器，实现验证码过滤
- OncePerRequestFilter：保证每次只被调用一次
- InitializingBean:初始化urls值

```java
// 重写afterPropertiesSet方法
// 在配置完毕后读取配置信息中url，导入需要过滤的项，url是BrowserSecurityConfig中传入的
// 此方法重写的是InitializingBean
    private Set<String> urls = new HashSet<>();
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        String[] configUrls = StringUtils
                .splitByWholeSeparatorPreserveAllTokens(
                        securityProperties
                                .getCode().getImage().getUrl()
                        , ","); // 按逗号拆分url，对应如上properties配置
        Collections.addAll(this.urls, configUrls);
        this.urls.add("/authentication/form");// 添加必定过项
    }
```

过滤器重写，一般情况下，定义自己的异常类：

```java
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response
            , FilterChain filterChain) throws ServletException, IOException {
        boolean action = false;
        // 判断当前请求是否需要进行验证码验证
        for (String url : urls) {
            if (antPathMatcher.match(url, request.getRequestURI())) {
                action = true;
                break;
            }
        }
        if (action) {
            try {
                validate(new ServletWebRequest(request)); // 通过(ImageCode) sessionStrategy.getAttribute(request, ValidateCodeController.SESSION_KEY);验证当前验证码
            } catch (ValidateCodeException e) {
                // 返回错误信息
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);// authenticationFailureHandler是过滤配置也是BrowserSecurityConfig中传入的
                return;
            }
        }
        filterChain.doFilter(request, response); // 放行
    }
```

配置BrowserSecurityConfig：

```java
ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
// 传入错误处理类
validateCodeFilter.setAuthenticationFailureHandler(ykAuthenticationFailureHandler);
// 传入配置信息
validateCodeFilter.setSecurityProperties(securityProperties);
// 调用afterProperties
validateCodeFilter.afterPropertiesSet(); 
http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
```