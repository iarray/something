package com.iarray.juc;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
    public static void main(String[] args) {

        //倒计时3秒后，起跑
        CountDownLatch countDownLatch = new CountDownLatch(3);

        for (int i=1;i<=5;++i) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "， 各就各位。。");
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "，起跑 !");

            }, "运动员" + i).start();
        }

        new Thread(()->{
            System.out.println("裁判开始倒数...");
            System.out.println("裁判：预备");
            for (int i = 3;i>0;i--){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(i + "...");
                countDownLatch.countDown();
            }
            System.out.println("裁判：跑");
        }).start();
    }
}
