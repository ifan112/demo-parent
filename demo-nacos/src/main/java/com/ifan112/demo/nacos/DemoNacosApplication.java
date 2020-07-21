package com.ifan112.demo.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;

import java.util.concurrent.Executor;

public class DemoNacosApplication {

    public static void main(String[] args) {

        String serverAddr = "";
        String dataId = "demo-nacos";
        String group = "DEFAULT_GROUP";

        try {
            ConfigService configService = NacosFactory.createConfigService("127.0.0.1");

            String demoNacosConfigContent = configService.getConfig(dataId, group, 1000);
            System.out.println(demoNacosConfigContent);

            // 监听配置变化
            configService.addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    System.out.println(configInfo);
                }
            });

        } catch (NacosException e) {
            e.printStackTrace();
        }


        // try {
        //     NamingService namingService = NamingFactory.createNamingService(serverAddr);
        //
        //
        //
        // } catch (NacosException e) {
        //     e.printStackTrace();
        // }
        //
        //
        // while (true) {
        //     try {
        //         Thread.sleep(1000);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        // }

    }


}
