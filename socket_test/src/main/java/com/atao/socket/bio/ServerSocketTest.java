package com.atao.socket.bio;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO 模拟实现服务端，每个线程对应一个连接
 */
public class ServerSocketTest {
    public static void main(String[] args) throws Exception {
        // 监听客户端的连接
        ServerSocket serverSocket = new ServerSocket(8090);
        // System.out.println("connect");
        // 不断监听客户端的连接
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("connect");

            // 单开一个线程拿这个socket,来一个客户端开一个线程，为了避免阻塞主线程，所以每来一个连接就开一个线程
            new Thread(new Runnable() {

                Socket client;

                public Runnable setClient(Socket socket1) {
                    client = socket1;
                    return this;
                }

                @Override
                public void run() {
                    try {
                        InputStream inputstream = client.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream));

                        while (true) {
                            System.out.println(Thread.currentThread().getName() + "======" + bufferedReader.readLine());
                        }
                    } catch (Exception e) {

                    }
                }
            }.setClient(socket)).start();
        }

        // 拿到客户端的socket之后，打印出来，客户端发给我们的是什么
    }
}
