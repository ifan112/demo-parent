package com.ifan112.demo.nacos.spring;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosProperty;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "127.0.0.1"))
// @NacosPropertySource(dataId = "demo-nacos", autoRefreshed = true)
@NacosPropertySource(dataId = "demo-nacos", type = ConfigType.JSON)
public class DemoNacosSpringConfiguration {

    @NacosValue(value = "${server.port}")
    private Integer port;

    public Integer getPort() {
        return port;
    }

    // private String content;
    //
    // public String getContent() {
    //     return content;
    // }
}
