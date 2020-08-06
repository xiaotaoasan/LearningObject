package com.atao.socket.bio.bioday01;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class TimeServer {
    public static void main(String[] args) {
        int port =8080;
        try {
            ServerSocket serverSocket = new ServerSocket(port); // 开启监听端口
            Socket socket = null;


            while (true){
                socket = serverSocket.accept();

                new Thread(new TimeServerHandler(socket)).start();

            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

    }
}


