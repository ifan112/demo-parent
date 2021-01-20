package com.ifan112.demo;

public class B {

    private A a;

    public B(A a) {
        this.a = a;
    }

    public void b() {
        System.out.println(a.a());
        System.out.println("B:b");
    }
}
