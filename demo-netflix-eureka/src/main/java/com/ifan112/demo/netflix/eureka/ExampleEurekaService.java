package com.ifan112.demo.netflix.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

public class ExampleEurekaService {

    public static void main(String[] args) {

        // 由 EurekaInstanceConfig 创建 ApplicationInfoManager
        MyDataCenterInstanceConfig instanceConfig = new MyDataCenterInstanceConfig();
        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();

        ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);

        // 由 EurekaClientConfig 和 ApplicationInfoManager 创建 DiscoveryClient，最终委托给基于 HttpClient 组件的 JerseyApplicationClient
        DefaultEurekaClientConfig clientConfig = new DefaultEurekaClientConfig();
        EurekaClient eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);


        // 从 eureka server 查询服务实例信息
        Application application = eurekaClient.getApplication("demo-springcloud-a");
        // 遍历服务实例列表
        application.getInstances().forEach(info -> {
            System.out.println(info.getIPAddr());
        });

        // DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory.getInstance();
        // ExampleServiceBase exampleServiceBase = new ExampleServiceBase(applicationInfoManager, eurekaClient, configInstance);
        // try {
        //     exampleServiceBase.start();
        // } finally {
        //     // the stop calls shutdown on eurekaClient
        //     exampleServiceBase.stop();
        // }
    }
}
