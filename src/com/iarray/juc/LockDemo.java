package com.iarray.juc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {

    private static volatile int flag = 0; //0.打印A 1.打印B 2.打印C

    public static void main(String[] args) {

        ReentrantLock lock = new ReentrantLock();

        Condition conditionA = lock.newCondition();

        Condition conditionB = lock.newCondition();

        Condition conditionC = lock.newCondition();

        int loopTimes = 10;

        new Thread(()->{

            for (int i=1;i<=loopTimes;++i){
                try{
                    lock.lock();
                    while (flag != 0){
                        conditionA.await();
                    }
                    System.out.println("=====  第" + i + "轮  =====");
                    for (int j=0; j<5;++j){
                        System.out.println("A" );
                    }
                    flag = 1;
                    conditionB.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }

        }, "A").start();

        new Thread(()->{

            for (int i=1;i<=loopTimes;++i){
                try{
                    lock.lock();
                    while (flag != 1){
                        conditionB.await();
                    }
                    System.out.println("=====  第" + i + "轮  =====");
                    for (int j=0; j<10;++j){
                        System.out.println("B" );
                    }
                    flag = 2;
                    conditionC.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }

        }, "B").start();

        new Thread(()->{

            for (int i=1;i<=loopTimes;++i){
                try{
                    lock.lock();
                    while (flag != 2){
                        conditionC.await();
                    }
                    System.out.println("=====  第" + i + "轮  =====");
                    for (int j=0; j<15;++j){
                        System.out.println("C" );
                    }
                    flag = 0;
                    conditionA.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }

        }, "C").start();


    }
}
