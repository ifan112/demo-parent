package com.ifan112.demo.mybatis.spring;

import com.ifan112.demo.mybatis.spring.entity.User;
import com.ifan112.demo.mybatis.spring.service.UserService;
import org.junit.Assert;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;

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
