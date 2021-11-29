package com.iarray.juc;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteDemo {

    public static void main(String[] args) {

        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();

        for (int i=1;i<10;++i) {
            final int num = i;
            new Thread(() -> {
                int j = 1;
                while (true) {
                    try {
                        list.add(num*j++);
                        Thread.sleep(1);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }).start();
        }

        for (int i=1;i<10;++i) {
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(1);
                        list.remove(0);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }).start();
        }

        new Thread(() -> {
            while (true){
                int c = 0;
                int size = list.size();
                int lastSize = list.size();
                //for (Integer i : list) {
                int i=0;
                for (; i<list.size();++i) {
                    c++;
                    // System.out.println(i);
//                    int s = list.size();
//                    if (lastSize != s){
//                        lastSize = s;
//                        System.out.println("遍历元素个数=" + lastSize + ", 遍历集合size=" + size);
//                    }
                    lastSize = list.size();
                }

                if (i != lastSize){
                    System.out.println("遍历次数和当时集合size不一致。 遍历元素个数=" + i + ", 退出时的size= " + lastSize);
                }

                int curSize = list.size();
                if (i != curSize) {
                    System.out.println("遍历次数和现在集合size不一致， 遍历元素个数=" + i + " 现在遍历集合size=" + curSize);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
