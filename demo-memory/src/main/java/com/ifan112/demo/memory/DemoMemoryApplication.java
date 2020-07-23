package com.ifan112.demo.memory;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.*;
import java.util.Arrays;
import java.util.List;

public class DemoMemoryApplication {

    public static void main(String[] args) {
        // memory();
        // garbageCollector();

        // thread();


        // com.ifan112.demo.memory.DemoMemoryApplication
        // -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:56588,suspend=y,server=n
        // -javaagent:/Users/ifan/Library/Caches/IntelliJIdea2019.3/captureAgent/debugger-agent.jar
        // -Dfile.encoding=UTF-8

        try {
            Process process = Runtime.getRuntime().exec("jps -vml");

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 采集数据
        Runtime runtime = Runtime.getRuntime();

        /*
         * 当前进程可从宿主机申请到的最大内存
         * maxMemory: 3817865216 bytes = 3641Mib = 3.56Gib
         *
         * 当前进程已经申请了的内存
         * totalMemory: 257425408 bytes = 245.5Mib
         *
         * 当前进程还没有使用的内存
         * freMemory: 253375144 bytes = 241.64Mib
         *
         * 因此，当前进程实际使用的内存是 (257425408-253375144) bytes = 3.86Mib
         */
        System.out.println("maxMemory: " + runtime.maxMemory());
        System.out.println("totalMemory: " + runtime.totalMemory());
        System.out.println("freeMemory: " + runtime.freeMemory());


        while (true) {
            System.out.println("maxMemory: " + runtime.maxMemory() / 1024 / 1024);
            System.out.println("totalMemory: " + runtime.totalMemory() / 1024 / 1024);
            System.out.println("freeMemory: " + runtime.freeMemory() / 1024 / 1024);


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println();
        }

    }

    private static void thread() {
        // 自定义线程
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();


        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        System.out.println(threadMXBean.getThreadCount());

        /*
         * Signal Dispatcher
         * Finalizer
         * Reference Handler
         *
         * main
         */
        for (long threadId : threadMXBean.getAllThreadIds()) {
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);
            System.out.println(threadInfo.getThreadName());
        }
    }

    private static void garbageCollector() {
        /*
         * PlatformManagedObject
         * -- MemoryManagerMXBean
         *    -- GarbageCollectorMXBean
         *
         */

        System.gc();

        /*
         * PS Scavenge
         * [PS Eden Space, PS Survivor Space]
         * collectionCount: 1
         * collectionTime: 2
         *
         * PS MarkSweep
         * [PS Eden Space, PS Survivor Space, PS Old Gen]
         * collectionCount: 1
         * collectionTime: 5
         */
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        garbageCollectorMXBeans.forEach(bean -> {
            System.out.println(bean.getName());
            System.out.println(Arrays.asList(bean.getMemoryPoolNames()));
            System.out.println("collectionCount: " + bean.getCollectionCount());
            System.out.println("collectionTime: " + bean.getCollectionTime());

            System.out.println();
        });
    }

    private static void memory() {
        /*
         * 内存管理器及其管理的内存区域
         *
         * CodeCacheManager
         * [Code Cache]
         *
         * Metaspace Manager
         * [Metaspace, Compressed Class Space]
         *
         * PS Scavenge
         * [PS Eden Space, PS Survivor Space]
         *
         * PS MarkSweep
         * [PS Eden Space, PS Survivor Space, PS Old Gen]
         */
        List<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
        memoryManagerMXBeans.forEach(bean -> {
            System.out.println(bean.getName());
            System.out.println(Arrays.asList(bean.getMemoryPoolNames()));

            System.out.println();

        });


        /*
         * Heap Memory Usage: init = 268435456(262144K) used = 8077856(7888K) committed = 257425408(251392K) max = 3817865216(3728384K)
         * Non-heap Memory Usage: init = 2555904(2496K) used = 6100104(5957K) committed = 8060928(7872K) max = -1(-1K)
         */
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        System.out.println("Heap Memory Usage: " + memoryMXBean.getHeapMemoryUsage());
        System.out.println("Non-heap Memory Usage: " + memoryMXBean.getNonHeapMemoryUsage());

        System.out.println();


        /*
         * Non-heap memory : Code Cache
         * [CodeCacheManager]
         *
         * Non-heap memory : Metaspace
         * [Metaspace Manager]
         *
         * Non-heap memory : Compressed Class Space
         * [Metaspace Manager]
         *
         * Heap memory : PS Eden Space
         * [PS MarkSweep, PS Scavenge]
         *
         * Heap memory : PS Survivor Space
         * [PS MarkSweep, PS Scavenge]
         *
         * Heap memory : PS Old Gen
         * [PS MarkSweep]
         */
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        memoryPoolMXBeans.forEach(bean -> {
            // 内存区域类型和名称
            System.out.println(bean.getType() + " : " + bean.getName());
            // 管理这一块内存区域的管理器列表
            System.out.println(Arrays.asList(bean.getMemoryManagerNames()));

            System.out.println();
        });
    }
}
