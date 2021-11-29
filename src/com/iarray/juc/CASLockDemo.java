package com.iarray.juc;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class CASLockDemo {

    static ConcurrentLinkedQueue<Thread> waitLockQueue = new ConcurrentLinkedQueue<Thread>();

    //锁状态： 0空闲 1占用
    static AtomicInteger lockState = new AtomicInteger(0);

    public static void main(String[] args) {

        // Unsafe.getUnsafe().compareAndSwapInt()

        //通过cas模拟实现多线程锁竞争现象
        for (int i=0; i<10;++i) {
            final Thread t = new Thread(()->{
                for (;;) {
                    lock();
                    //模拟过了2秒， 释放锁
                    sleep(1000);

                    unlock();

                    sleep(1000);
                }
            },"线程" + i);
            t.start();

        }

    }

    private static void lock()
    {
        //自旋方式抢占锁
        for (;;){
            if (lockState.compareAndSet(0, 1)){
                System.out.println(Thread.currentThread().getName() + ": 抢到了了锁");
                break;
            }else {
                //抢不到锁就把自己放进队列里面， 然后挂起当前线程
                waitLockQueue.add(Thread.currentThread());
                LockSupport.park();
            }
        }
    }

    private static void unlock()
    {
        for (;;) {
            //释放锁
            if (lockState.compareAndSet(1, 0)) {
                System.out.println(Thread.currentThread().getName() + ": 释放了锁");
                //取队列的下一个线程唤醒
                Thread t = waitLockQueue.poll();
                if (t!=null){
                    LockSupport.unpark(t);
                }
                break;
            }
        }
    }

    private static void sleep(long delay)
    {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
