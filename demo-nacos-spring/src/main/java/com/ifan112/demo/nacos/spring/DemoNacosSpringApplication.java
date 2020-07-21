package com.ifan112.demo.nacos.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DemoNacosSpringApplication {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext
                = new AnnotationConfigApplicationContext(DemoNacosSpringConfiguration.class);


        DemoNacosSpringConfiguration configuration = applicationContext.getBean(DemoNacosSpringConfiguration.class);
        System.out.println(configuration.getPort());

    }
}
