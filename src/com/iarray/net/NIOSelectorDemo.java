package com.iarray.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOSelectorDemo {
    public static void main(String[] args) {
        try {
            ServerSocketChannel server = ServerSocketChannel.open();
            //配置服务端为非阻塞IO
            //底层调用了fcntl(fd,F_SETFL, O_RDWR|O_NONBLOCK)
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(7000));

            //底层的EPollArrayWrapper.java类调用了epoll_create
            Selector selector = Selector.open();
            //底层调用了epoll_ctl 注册
            server.register(selector, SelectionKey.OP_ACCEPT);

            while (true){
                //底层调用了epoll_wait 等待IO事件
                //select可以传入一个timeout， 默认不传时会阻塞等待IO事件
                System.out.println("阻塞等待IO事件.");
                if (selector.select() > 0){
                    // System.out.println("有IO事件.");
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator it = keys.iterator();
                    while (it.hasNext()){
                        SelectionKey k =  (SelectionKey)it.next();
                        //处理一个事件就删除一个， 不移除的话会重复处理。
                        it.remove();
                        if (k.isAcceptable()){
                            System.out.println("客户端连接.");
                            acceptClient(server, selector);
                        }
                        if (k.isReadable()){
                            System.out.print("客户端发送数据:");
                            handleRead(k);
                        }
                    }
                }else {
                    //如果不调用it.remove(); 这一行有可能会被打印
                    System.out.println("除非我select加了timeout, 否则这里应该永远不会被打印.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptClient(ServerSocketChannel server, Selector selector){
        try {
            SocketChannel client = server.accept();
            //配置客户端为非阻塞IO
            client.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);
            //注册客户端的读取事件
            //相当于调用了epoll_ctl 注册
            client.register(selector, SelectionKey.OP_READ, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRead(SelectionKey key){
        SocketChannel client = (SocketChannel)key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        try {
            int size = 0;
            while ((size = client.read(buf)) > 0)
            {
                buf.flip();
                System.out.print(new String(buf.array(), 0, size));
                buf.clear();
            }

            //这里很重要 ，如果客户端异常断开， 则select就会不停触发readable，这时就不是一个阻塞等待IO事件的状态， CPU占用就会拉满， 那时因为key没有被cancel
            //所以我们读到-1的时候则认为客户端异常断开，抛出异常，使key从epoll取消注册
            if (size == -1)
            {
                throw new IOException("客户端异常断开");
            }
        } catch (IOException e) {
            //关键
            key.cancel();
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            System.out.println("客户端断开连接.");
        }
    }
}
