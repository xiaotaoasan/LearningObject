package com.atao.socket.newio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class NIOTest {
    public static void main(String[] args) throws Exception {
        LinkedList<SocketChannel> socketChannelLinkedList = new LinkedList<>(); // 保存所有连接了服务端的客户端的socket，后面需要遍历
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9090));  // 绑定一个端口号
        serverSocketChannel.configureBlocking(false); // 默认是阻塞的。需要设置为非阻塞
        while (true) {
            // 第一步接受连接，添加到list里面
            try {
                Thread.sleep(3000);
                SocketChannel socketChannel = serverSocketChannel.accept();  // 获取连接

                if (socketChannel == null) {
                    System.out.println("没有客户端连接");
                } else {
                    System.out.println("获取到连接");
                    // 将连接设置为非阻塞的，因为在receive的时候默认也是阻塞的，将连接添加到链表中，然后后面去循环这个链表
                    socketChannel.configureBlocking(false);
                    socketChannelLinkedList.add(socketChannel);
                }

                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4096);

                // 第二步，循环连接，然后取出socketchannel
                for (SocketChannel socketChannel1 : socketChannelLinkedList) {

                    int num = socketChannel1.read(byteBuffer);
                    if (num > 0) {
                        byteBuffer.flip();
                        byte[] a = new byte[byteBuffer.limit()];
                        byteBuffer.get(a);
                        System.out.println(new String(a));
                        byteBuffer.clear();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }

        }
    }
}
