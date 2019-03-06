# Spring集成Mybatis

### 前言


​	之前，我们是通过mybatis的`SqlSession`去获取一个`UserMapper`，然后调用该接口下的方法获取执行结果。例如：


```java
@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user(id, name, age) VALUE(#{id}, #{name}, #{age})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    long insert(User user);
    
    // 由mapper文件中的sql实现
    User selectOne(long id);
}
```

```java
public class Application {
    public static void main(String[] args) throws Exception {
        // mybatis配置文件
        Path mybatisConfigPath = Paths.get("demo-mybatis/src/main/resources/mybatis-config.xml");
        InputStream mybatisConfigIn = Files.newInputStream(mybatisConfigPath);

        // 读取配置文件，创建sqlSession工厂，即SqlSessionFactory对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(mybatisConfigIn);

        // 打开一个到数据库的连接
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // mybatis基于Jdk动态代理，生成了一个UserMapper的动态代理类
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        // 断言userMapper的类类型实际上是一个Jdk动态代理类，通过
        Assert.assertTrue(Proxy.isProxyClass(userMapper.getClass()));

        // 执行UserMapper接口下的方法
        User user = userMapper.selectOne(1L);

        // .. UserMapper其它方法
        
        sqlSession.commit();
        sqlSession.close();
        
        mybatisConfigIn.close();
    }
}

```

> ​	当然，我们已经知道实际上获取到的其实是一个基于`org.apache.ibatis.binding.MapperProxy`创建出来的动态代理类的对象。简而言之，它实现了`UserMapper`和`InvokeHandler`接口，对`UserMapper`接口的任何方法调用都会委托到该对象的`invoke()`方法，此后它根据被调用的方法名称去查找与之相对应的SQL并执行，最后返回执行结果。
>

​	在实际的Spring项目中，Mybatis的Mapper通常作为操作数据库的DAO（Data Access Object）层组件被注入到服务层的实现类中。例如：

```java
public interface UserService {
    boolean save(User user);
    User get(long id);
}

@Service
public class UserServiceImpl implements UserService {
    
    /**
     * UserMapper作为DAO组件，用来更新数据库中的数据
     */
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public boolean save(User user) {
        return userMapper.insert(user) == 1;
    }
    
    @Override
    public User get(long id) {
        return userMapper.selectOne(id);
    }
}
```

​	为了满足自动注入的特性，显然此时无法简单地通过`sqlSession.getMapper()`的方式来获取Mapper的代理对象，它必然要交给Spring去创建和管理。而Mapper是依赖于`SqlSession`的，这是Mybatis的核心逻辑不会改变。因此，连同Mapper一起被Spring管理的一定还有`SqlSession`。此前由`SqlSession`的`commit()`和`rollback()`来控制的事务也无法使用，转而交由Spring事务机制去控制。

​	另外，数据源`DataSource`也将会由Spring管理。

​	总结一下，为了集成Mybatis，Spring需要托管的bean有：Mapper、`SqlSession`和`DataSource`。



### 实践

#### 创建数据表

```sql
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  name VARCHAR(120) COMMENT '用户名',
  age INT COMMENT '年龄',
  status INT COMMENT DEFAULT 1 '状态',
  last_changed TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '最近修改时间'
) DEFAULT CHARSET UTF8 COMMENT '用户表';
```



#### 创建maven项目

```
pom.xml
src/main/
	java/
		com.ifan112.demo.mybatis.spring/
			entity/
				User.java
			mapper/
				UserMapper.java(I
			service/
				impl/
					UserServiceImpl.java
				UserService.java(I
			DemoMybatisSpringApplication.java
	resources/
		mappers/
			user-mapper.xml
		application-context.xml
		db.properties
```



#### 引入依赖

```xml
<!-- mybatis与spring集成的依赖库 -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>2.0.0</version>
</dependency>
<!-- mybatis-spring依赖于spring-jdbc，将mybatis事务委托给spring -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.1.0.RELEASE</version>
</dependency>

<!-- 见名知意的依赖 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.1.0.RELEASE</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.38</version>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.6</version>
</dependency>
```



#### 编码

Entity、Mapper、Service等代码内容[参考源码](https://github.com/ifan112/demo-parent/tree/49feb31199855bdb8adf83fcde1a9bfc2da4b16f/demo-mybatis-spring)



#### 添加spring配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 自动扫描和注册Service等组件。Spring自身是不能识别Mapper将其用作组件的 -->
    <context:component-scan base-package="com.ifan112.demo.mybatis.spring.service"/>

    <!-- 读取属性文件。声明数据库连接池时通过占位符引用这些属性 -->
    <context:property-placeholder location="db.properties"/>

    <!-- 声明数据库连接池 -->
    <bean id="dataSource" class="org.apache.ibatis.datasource.unpooled.UnpooledDataSource">
        <property name="driver" value="${driver}"/>
        <property name="url" value="${url}"/>
        <property name="username" value="${username}"/>
        <property name="password" value="${password}"/>
    </bean>

    <!-- SqlSessionFactoryBean是一个FactoryBean，用于SqlSessionFactory -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!-- 创建SqlSessionFactory时一定需要知道dataSource和Mapper文件。其余设置将会保持默认 -->
        <property name="dataSource" ref="dataSource"/>
        <property name="mapperLocations" value="classpath:mappers/*"/>
    </bean>

    <!-- Mapper扫描器配置。
         在扫描到指定包下的Mapper之后，通过上面创建的SqlSessionFactory对象获取SqlSession，然后创建Mapper的动态代理对象，并注册到Spring中。
         当有其它组件依赖Mapper时，Spring就可以自定注入它所依赖的Mapper的动态代理对象了。
     -->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- Mapper所在包 -->
        <property name="basePackage" value="com.ifan112.demo.mybatis.spring.mapper"/>
    </bean>

</beans>
```



#### 启动

```java
public class DemoMybatisSpringApplication {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext
                = new ClassPathXmlApplicationContext("classpath:application-context.xml");

        UserService userService = (UserService) applicationContext.getBean("userServiceImpl");

        boolean r;

        for (int i = 1; i < 4; i++) {
            User u = new User();
            u.setId(i);
            u.setName("Name-" + i);
            u.setAge(22 + i);
            u.setStatus(1);
            u.setLastChanged(new Date());

            r = userService.save(u);
            Assert.assertTrue("保存失败！！", r);
        }

        User user = userService.get(2);
        Assert.assertNotNull(user);
        Assert.assertEquals("Name-2", user.getName());
        Assert.assertEquals(24, user.getAge());

        List<User> users = userService.allUsers();
        Assert.assertEquals(3, users.size());

        // 删除
        r = userService.delete(1);
        Assert.assertTrue("删除失败！！", r);

        r = userService.delete(2);
        Assert.assertTrue("删除失败！！", r);

        r = userService.delete(3);
        Assert.assertTrue("删除失败！！", r);

    }

}
```



### 源码分析