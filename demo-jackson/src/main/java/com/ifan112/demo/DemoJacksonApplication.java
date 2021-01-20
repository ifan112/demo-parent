package com.ifan112.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class DemoJacksonApplication {

    public static void main(String[] args) {
        List<Module> modules = ObjectMapper.findModules();
        for (Module module : modules) {
            System.out.println(module.getModuleName());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        // 注册扩展的序列化模块
        objectMapper.registerModule(new JavaTimeModule());
        // 反序列化时忽略未知的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时忽略 null 值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        User user = new User();
        user.setId(1);
        user.setName("Hi");
        user.setDateTime(LocalDateTime.now());
        user.setInterests(Arrays.asList("movie", "book"));

        try {
            System.out.println(objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            URL userJsonURL = DemoJacksonApplication.class.getClassLoader().getResource("users.json");
            Path usersJsonPath = Paths.get(userJsonURL.getPath());

            List<User> users = objectMapper.readValue(usersJsonPath.toFile(), new TypeReference<List<User>>() {});

            System.out.println(users.size());
            User x = users.get(0);
            System.out.println(x);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    static class User {
        private Integer id;
        private Integer age;
        private String name;
        private LocalDateTime dateTime;

        private List<String> interests;

        public User() {
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }

        public List<String> getInterests() {
            return interests;
        }

        public void setInterests(List<String> interests) {
            this.interests = interests;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", age=" + age +
                    ", name='" + name + '\'' +
                    ", dateTime=" + dateTime +
                    ", interests=" + interests +
                    '}';
        }
    }

}
