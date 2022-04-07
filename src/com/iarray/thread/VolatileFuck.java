package com.iarray.thread;

import java.util.Arrays;

public class VolatileFuck
{
    private static boolean running1 = true;

    private static volatile boolean running2 = true;


    public static void main(String[] args) {
        new Thread(()->{
            while (running1){
                //System.out.println("running1");
            }
            //这里不会被打印 ，其它线程修改running1=false后本线程不会停止
            System.out.println("stop running1");
        }).start();

        new Thread(()->{
            while (running2){
                //System.out.println("running2");
            }
            System.out.println("stop running2");
        }).start();


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        running1 = false;
        System.out.println("set running1 = false");
        running2 = false;
        System.out.println("set running2 = false");
    }
}
