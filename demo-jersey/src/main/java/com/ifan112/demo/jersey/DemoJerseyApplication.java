package com.ifan112.demo.jersey;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache4.ApacheHttpClient4;

public class DemoJerseyApplication {

    public static void main(String[] args) {
        /*
         * 简单来说，Jersey Client 是一个实现了 JSR-311 即 RESTful 规范的 HTTP 请求客户端，
         * 默认情况下它基于 JDK 自带的 HTTPURLConnection 完成请求和响应。
         *
         * Apache HttpClient 是一个符合 HTTP 协议的客户端，但是不方便处理 RESTful 风格的请求。
         *
         * 因此将这两者结合，Jersey Client 的 API 定义 RESTful 风格的请求，然后将 HTTP 请求和响应
         * 委托给 Apache HttpClient 组件完成。
         */
        Client client = new ApacheHttpClient4();

        WebResource webResource = client.resource("http://www.baidu.com");

        String s1 = webResource.get(String.class);
        System.out.println(s1);
    }
}
