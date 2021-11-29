package com.iarray.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureDemo {

    static class MyCallable implements Callable<String>
    {

        @Override
        public String call() throws Exception {
            Thread.sleep(5000);
            return "Hello";
        }
    }

    public static void main(String[] args) {
        MyCallable callable= new MyCallable();
        FutureTask task = new FutureTask(callable);

        new Thread(task).start();
        long start = System.currentTimeMillis();
        System.out.println("开始=" + start);

        try {
            String str = (String) task.get();
            System.out.println(str);
            System.out.println("结束=" + System.currentTimeMillis() + ", 耗时=" + (System.currentTimeMillis() - start));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

}
