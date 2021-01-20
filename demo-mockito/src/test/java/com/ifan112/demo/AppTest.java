package com.ifan112.demo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

public class AppTest {

    @Test
    public void shouldAnswerWithTrue() {
        A a = Mockito.mock(A.class);
        Mockito.when(a.a())
                .thenReturn("Hello");

        B b = new B(a);
        b.b();
    }
}
