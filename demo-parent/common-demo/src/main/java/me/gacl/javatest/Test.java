package me.gacl.javatest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * 多任务并行处理
 * @author yangzhenyu
 * 经典版
 * */
public class Test {
    public static void main(String[] args) {
        int size = 5;
        CountDownLatch latch = new CountDownLatch(size);
        System.out.println("开始执行子任务 ============start");
        for (int i = 0; i < size; i++) {
            Executors.newFixedThreadPool(size).submit(() -> {
                try {
                    // long running task
                    System.out.println("开始执行子任务【"+Thread.currentThread().getName() + " " + latch.getCount()+"】");
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// continue...
        System.out.println("子任务 全部执行结束,主线程继续执行【"+Thread.currentThread().getName()+"】");
    }
}
