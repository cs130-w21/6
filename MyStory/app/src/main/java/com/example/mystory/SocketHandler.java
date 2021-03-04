package com.example.mystory;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketHandler {
    private final int SEGMENT_SIZE = 65000;
    private final String SERVER = "2.tcp.ngrok.io";
    private final int SERVER_PORT = 12082;

    private Socket mSocket;

    public SocketHandler() {
        Log.d("MyStory", "setup socket");
        try {
            InetAddress serverAddress = InetAddress.getByName(SERVER);
            Log.d("MyStory", "server address: " + serverAddress.toString());
            mSocket = new Socket(serverAddress, SERVER_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("MyStory", "socket ready");
    }

    public void writeToSocket(char[] jsonString) {
        int requestLength = jsonString.length;
        int offset = 0;
        int bytesWrite;

        try {
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(mSocket.getOutputStream()), SEGMENT_SIZE);
            while (offset < requestLength) {
                bytesWrite = Math.min(requestLength - offset, SEGMENT_SIZE);
                out.write(jsonString, offset, bytesWrite);
                offset += bytesWrite;
            }
            out.flush();
            out.write(";", 0, 1);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Log.d("MyStory", new String(jsonString, 0, requestLength));
        Log.d("MyStory", "finished sending");
    }

    public int readFromSocket(char[] jsonString) {
        int offset = 0;
        int bytesRead;
        int bufferSize = jsonString.length;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            bytesRead = in.read(jsonString, offset, bufferSize);
            while (bytesRead > 0) {
                offset += bytesRead;
                bufferSize -= bytesRead;
                if (jsonString[offset - 1] == ';') {
                    break;
                }
                bytesRead = in.read(jsonString, offset, bufferSize);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("MyStory", "finished reading");
        // Log.d("MyStory", new String(jsonString, 0, offset));
        return offset - 1;
    }
}
