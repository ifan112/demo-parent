package com.ifan112.demo;

import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class App {

    private static final Logger logger = LogManager.getLogManager().getLogger("");

    public static void main(String[] args) {
        // JUL 默认使用 System.err 输出日志
        logger.info("Hello");
        logger.info("Hello2");
    }
}
