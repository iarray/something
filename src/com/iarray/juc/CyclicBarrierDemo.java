package com.iarray.juc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {

    public static void main(String[] args) {
        String[] family = new String[]{"弟弟","妹妹","姐姐", "哥哥", "爸爸", "妈妈"};


        CyclicBarrier cyclicBarrier = new CyclicBarrier(family.length,()->{
            System.out.println("人到齐了，起筷 ！");
        });

        for (int i=0;i<family.length; i++){
            final int waitSecond = i * 1000;
            new Thread(()->{
                try {
                    Thread.sleep(waitSecond);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "回家了，等人齐吃饭...");

                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread().getName() + " 开吃 ！");

            }, family[i]).start();
        }
    }

}
