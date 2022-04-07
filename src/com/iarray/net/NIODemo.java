package com.iarray.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NIODemo {

    public static void main(String[] args) {
//        nio1();
        nio2();
    }

    private static void nio1()
    {

        try {
            LinkedList<SocketChannel> clients = new LinkedList<>();
            ByteBuffer readBuf = ByteBuffer.allocate(1024);
            ServerSocketChannel server = ServerSocketChannel.open();
            //配置非阻塞IO
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(6000));

            //优点： 单线程能处理大量的连接和读写 （这里的大量还不算特别大，有上限）
            /*缺点： 1. CPU 跑满了， 如果没有任何连入， 或者连入的客户端没有发送数据， CPU多数情况下做无用功
                    2. 客户端很多时，会造成遍历客户端读取耗时太长，导致无法处理新的客户端连接。 （这里可以加入线程池优化， 但是还是解决不了无效读操作的问题）
             */
            while (true){
                SocketChannel client = server.accept();
                if (client != null){
                    System.out.println("客户端连入.");

                    //配置客户端非阻塞IO
                    //这行是关键， 否则下面的读操作也是阻塞的。
                    client.configureBlocking(false);
                    clients.add(client);
                }

                //遍历读客户端数据
                for (SocketChannel c :  clients) {
                    try {
                        int readSize = c.read(readBuf);
                        while (readSize > 0) {
                            System.out.print(new String(readBuf.array(), 0, readSize));
                            readBuf.clear();
                            readSize = c.read(readBuf);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void nio2()
    {
        CopyOnWriteArrayList<SocketChannel> clients = new CopyOnWriteArrayList<>();
        new Thread(()->{
            try {
                ServerSocketChannel server = ServerSocketChannel.open();
                //配置非阻塞IO
                server.configureBlocking(false);
                server.bind(new InetSocketAddress(6000));

                //优点： 单线程能处理大量的连接和读写 （这里的大量还不算特别大，有上限）
            /*缺点： 1. CPU 跑满了， 如果没有任何连入， 或者连入的客户端没有发送数据， CPU多数情况下做无用功
                    2. 客户端很多时，会造成遍历客户端读取耗时太长，导致无法处理新的客户端连接。 （这里可以加入线程池优化， 但是还是解决不了无效读操作的问题）
             */
                while (true){
                    SocketChannel client = server.accept();
                    if (client != null){
                        System.out.println("客户端连入.");

                        //配置客户端非阻塞IO
                        //这行是关键， 否则下面的读操作也是阻塞的。
                        client.configureBlocking(false);
                        clients.add(client);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        //另外开一条线程处理读取
        new Thread(()->{
            ForkJoinPool customThreadPool = new ForkJoinPool(4);
            //AtomicInteger times = new AtomicInteger();
            while (true){
                //遍历读客户端数据
                try {
                    customThreadPool.submit(()-> clients.parallelStream().filter(SocketChannel::isConnected).forEach(c->{
                        try {
                            ByteBuffer readBuf = ByteBuffer.allocate(1024);
                            int readSize = c.read(readBuf);
                            while (readSize > 0) {
                                System.out.println("read on thread=" + Thread.currentThread().getName());
                                System.out.print(new String(readBuf.array(), 0, readSize));
                                readBuf.clear();
                                readSize = c.read(readBuf);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })).get();
                    //System.out.println("第" + times.incrementAndGet() + "轮遍历");
                    //Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
