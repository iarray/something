package com.iarray.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class BIODemo {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5000);
            while (true) {
                //阻塞监听客户端接入
                Socket socket = server.accept();

                //这样些，如果客户端不发送任何数据， 则会阻塞当前线程。导致下一个客户端无法连入， 解决方法通常是把客户端的读操作单独开线程处理
                //handleClientSocket(socket);

                //开线程处理客户端事件, 这是最经典的bio模型， 但是存在问题，如果连接数过多会导致线程数太多，系统资源严重浪费，导致宕机
                new Thread(()->{
                    handleClientSocket(socket);
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientSocket(Socket client)
    {
        try {
            InputStream input = client.getInputStream();
            byte[] buf = new byte[100];
            while (input.available() > 0) {
                int l = input.read(buf);
                buf[l] = '\0';
                System.out.println(new String(buf));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
