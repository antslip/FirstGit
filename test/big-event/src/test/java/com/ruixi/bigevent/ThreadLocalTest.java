package com.ruixi.bigevent;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class ThreadLocalTest {

    @Test
    public void testThreadLocal(){

        TL tl = new TL();
        new Thread(()->{
            tl.setName("萧炎");
            System.out.println(Thread.currentThread().getName() + ":" + tl.getName());
            System.out.println(Thread.currentThread().getName() + ":" + tl.getName());
            System.out.println(Thread.currentThread().getName() + ":" + tl.getName());
        },"蓝色").start();

        new Thread(()->{
            tl.setName("药尘");
            System.out.println(Thread.currentThread().getName() + ":" + tl.getName());
            System.out.println(Thread.currentThread().getName() + ":" + tl.getName());
            System.out.println(Thread.currentThread().getName() + ":" + tl.getName());
        },"绿色").start();
    }
}
