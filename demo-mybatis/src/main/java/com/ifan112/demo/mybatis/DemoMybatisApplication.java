package com.ifan112.demo.mybatis;

import com.ifan112.demo.mybatis.entity.User;
import com.ifan112.demo.mybatis.mapper.UserMapper;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DemoMybatisApplication {

    public static void main(String[] args) throws IOException {
        // mybatis配置文件
        Path mybatisConfigPath = Paths.get("demo-mybatis/src/main/resources/mybatis-config.xml");
        InputStream mybatisConfigIn = Files.newInputStream(mybatisConfigPath);

        /*
         * mybatis从三处获取属性列表：
         * 1. 配置文件中properties节点下指定的属性列表
         * 2. 配置文件中properties节点的resource/url属性指定的外部文件
         * 3. 开发者在构建SqlSessionFactory时指定的属性列表
         *
         * 由于后面指定的属性会覆盖前面指定的属性，因此，属性值的优先顺序从小到大是：
         * properties节点下属性值 < 外部文件中属性值 < 开发者传入的Properties中的属性值
         *
         * 为了演示这一项特性，分别在：
         * 1. 配置文件中properties节点下设置了username和password属性值
         * 2. 配置文件中properties节点的resource属性指定了引入的外部文件地址为db.properties，并在该文件中设置了driver属性值
         * 3. 最后，构造SqlSessionFactory时，传入了url属性值
         * 4. 在外部文件db.properties中，设置url属性值为非法的数据库地址。不过，3中的属性值将会覆盖它，保证该属性值合法可用
         *
         * mybatis合并这三处的所有属性。此后，在配置文件中可以通过 ${属性名称} 来获取到这些属性的值
         *
         * mybatis在解析xml配置文件时，逐个获取xml节点。如果该节点是text类型，则判断该节点的值是否是${属性名}格式的占位符
         * 如果是，则解析该占位符，获取属性列表中指定属性值
         *
         * 源码：{@link org.apache.ibatis.builder.xml.XMLConfigBuilder#propertiesElement(XNode)}
         *      {@link org.apache.ibatis.parsing.PropertyParser#parse(String, Properties)}
         */
        Properties customerProps = new Properties();
        customerProps.setProperty("url", "jdbc:mysql://localhost:3306/test?useSSL=true");

        // 创建SqlSession工厂类
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(mybatisConfigIn, customerProps);

        // 获取一个SqlSession，一个到MySQL数据库的连接
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        Assert.assertTrue("userMapper不是Jdk动态代理类！！", Proxy.isProxyClass(userMapper.getClass()));

        int rows;

        for (int i = 1; i < 4; i++) {
            User u = new User();
            u.setId(i);
            u.setName("Name-" + i);
            u.setAge(22 + i);
            u.setStatus(1);
            u.setLastChanged(new Date());

            rows = userMapper.save(u);
            Assert.assertEquals("保存失败！！", 1, rows);
        }

        User user = userMapper.selectOne(2);
        Assert.assertNotNull(user);
        Assert.assertEquals("Name-2", user.getName());
        Assert.assertEquals(24, user.getAge());

        List<User> users = userMapper.selectAll();
        Assert.assertEquals(3, users.size());

        // 删除
        rows = userMapper.delete(1);
        Assert.assertEquals("删除失败！！", 1, rows);

        rows = userMapper.batchDelete(Arrays.asList(2L, 3L));
        Assert.assertEquals("批量删除失败！！", 2, rows);

        // 提交所有操作
        sqlSession.commit();
        // 关闭与数据库的会话
        sqlSession.close();
    }

}
