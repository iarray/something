package com.iarray.juc;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("【线程1】调用park.");
            LockSupport.park();
            System.out.println("【线程1】被恢复了。");
        });
        thread.start();

        System.out.println("【线程1】在park前先调用了unpark， 看看会怎样.");
        LockSupport.unpark(thread);



        Object lock = new Object();
        Thread thread2 = new Thread(()->{
            synchronized (lock){
                try {
                    Thread.sleep(2000);
                    System.out.println("【线程2】调用wait");
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("【线程2】被恢复了。");
        });
        thread2.start();

        System.out.println("【线程2】在wait前调用notify， 看看会怎样.");
        synchronized (lock){
            lock.notify();
        }

    }
}
