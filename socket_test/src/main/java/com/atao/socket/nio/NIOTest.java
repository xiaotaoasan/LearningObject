package com.atao.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

public class NIOTest {
    public static void main(String[] args) {


        MutiplexTimeServer mutiplexTimeServer = new MutiplexTimeServer(8080);


        // 开一个线程来作为selector
        new Thread(mutiplexTimeServer).start();
    }
}

class MutiplexTimeServer implements Runnable {
    ServerSocketChannel serverSocketChannel;
    Selector selector;

    MutiplexTimeServer(int port) {
        try {
            selector = Selector.open(); // 多路复用起
            serverSocketChannel = ServerSocketChannel.open(); //监听开启
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            // 将服务端通道注册到多路复用起
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

    }

    @Override
    public void run() {

        while (true) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    handlerInput(key);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }

        }

    }

    private void handlerInput(SelectionKey selectionKey) {
        // 如果是服务端监听的接口
        if (selectionKey.isAcceptable()) {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();

            try {
                SocketChannel socketChannel = serverSocketChannel.accept();

                // 注册到多路复用起作为 读
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }

        if (selectionKey.isReadable()) {
            try {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                // 读到缓冲区
                int byteNum = socketChannel.read(byteBuffer);

                if (byteNum > 0) {
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String reslut = new String(bytes, "UTF-8");
                    System.out.println(reslut);
                    String currenttime = LocalDateTime.now().toString();
                    doWrite(socketChannel, currenttime);
                }
            } catch (Exception e) {


            } finally {
            }
        }
    }

    private void doWrite(SocketChannel socketChannel, String st) {
        byte[] bytes = st.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(st.getBytes().length);
        try {
            byteBuffer.put(bytes);
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        } catch (IOException e) {


        } finally {
        }

    }

}
