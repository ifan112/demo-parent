package com.ifan112.demo;

import org.mapstruct.factory.Mappers;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        CreateUserRequest request = CreateUserRequest.builder()
                .username("test")
                // .type(1)
                .build();

        // CreateUserRequest request = new CreateUserRequest();
        // request.setUsername("test");

        User user = UserMapper.INSTANCE.toUser(request);

        System.out.println(user.getUsername());

    }
}
