package com.iarray.thread;

import java.util.concurrent.CountDownLatch;

/**
 * 代码无序执行现象
 *
 */
public class DisorderDemo {

    private static int x, y , a, b;

    public static void main(String[] args) throws InterruptedException {

        long start = System.currentTimeMillis();
        int count = 0;
        while (true) {
            x = 0;
            y = 0;
            a = 0;
            b = 0;

            CountDownLatch countDownLatch = new CountDownLatch(2);
            Thread t1 = new Thread(() -> {
                a = 1;
                x = b;
                countDownLatch.countDown();
            });


            Thread t2 = new Thread(() -> {
                b = 1;
                y = a;
                countDownLatch.countDown();
            });

            t1.start();
            t2.start();
            countDownLatch.await();
            if (x == 0 && y == 0) {
                //耗时=2575138ms, 经过33244722次后， x=0,  y=0
                System.out.println("耗时=" + (System.currentTimeMillis() - start) + "ms, 经过" + count + "次后， x=0,  y=0");
                break;
            }

            count++;
        }
    }
}
