package com.iarray.juc;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreDemo {
    public static void main(String[] args) {
        int count = 3;
        System.out.println("总共有" + count + "个空闲车位");
        Semaphore semaphore = new Semaphore(count);
         new Thread(()->{
            System.out.println("正常人：进入停车场， 正在寻找车位。");
            if (semaphore.tryAcquire()){
                System.out.println("正常人：试着找车位，找到车位，停车。");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("正常人：离开停车场， 去逛街。");
                semaphore.release();
            }else {
                System.out.println("正常人：没有车位， 不等了...");
            }
        }).start();


        new Thread(()->{
            System.out.println("霸王车：我要占2个车位。");
            try {
                semaphore.acquire(2);

                System.out.println("霸王车：占到2个车位..");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("霸王车：离开停车场，空了2个车位。");
                semaphore.release(2);

            } catch (InterruptedException e) {

            }
        }).start();

        new Thread(()->{
            System.out.println("悠闲哥：等个1秒，看能不能等到车位。");
            try {
                if(semaphore.tryAcquire(1, TimeUnit.SECONDS)) {
                    System.out.println("悠闲哥: 1秒后，等到车位..");


                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("悠闲哥: 离开停车场， 去吃饭。");
                    semaphore.release();
                }else {
                    System.out.println("悠闲哥: 超过1秒还没车位，不等了。");
                }
            } catch (InterruptedException e) {

            }
        }).start();
    }
}
