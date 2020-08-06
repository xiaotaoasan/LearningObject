package com.atao.socket.bio.bioday01;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;

class TimeServerHandler implements Runnable {

    private Socket socket;

    TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            String currentTime = null;
            while (true) {
                String inRead = in.readLine();
                System.out.println("Client_message:"+inRead);
                if (inRead == null) {
                    break;
                }
                currentTime = "QUERY TIME ORER".equalsIgnoreCase(inRead) ? LocalDate.now().toString() : "error";
                printWriter.println(currentTime);


            }
        } catch (IOException e) {
            e.printStackTrace();
            // 有异常就关闭socket


        } finally {
        }
    }
}