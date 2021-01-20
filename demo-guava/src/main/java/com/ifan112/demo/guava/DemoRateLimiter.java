package com.ifan112.demo.guava;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Date;

public class DemoRateLimiter {
    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(50);

        rateLimiter.acquire(100);
        rateLimiter.acquire(200);
    }
}
