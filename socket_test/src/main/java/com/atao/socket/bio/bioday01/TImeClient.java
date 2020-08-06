package com.atao.socket.bio.bioday01;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TImeClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(System.in)); // 命令行输出用的
            String st = null;
            while ((st = bufferedReader1.readLine()) != null) {
                printWriter.println(st);
                String serverAccept = bufferedReader.readLine();
                System.out.println(serverAccept);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }
}
