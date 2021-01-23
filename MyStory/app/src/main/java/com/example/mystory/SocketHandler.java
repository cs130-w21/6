package com.example.mystory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketHandler {
    public final int UPLOAD = 1;
    public final int GET_QUOTE = 2;
    public final int DELETE = 3;
    public final int LOAD = 4;
    private final String SERVER_IP = "";
    private final int SERVER_PORT = -1;
    private final int SEGMENT_SIZE = 650000;

    private JSONObject mRequest;
    private int mOpCode;
    private Socket mSocket;

    SocketHandler(JSONObject request, int opCode) {
        mRequest = request;
        mOpCode = opCode;
        try {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            mSocket = new Socket(serverAddress, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handler(char[] jsonString) {
        if (mOpCode == UPLOAD || mOpCode == DELETE) {
            serverWrite(mRequest.toString().toCharArray());
        } else {
            serverWrite(mRequest.toString().toCharArray());
            serverRead(jsonString);
        }
    }

    private void serverWrite(char[] jsonString) {
        BufferedWriter out;
        int length = jsonString.length;
        int offset = 0;

         try {
             out = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()),
                     SEGMENT_SIZE);
             while (offset < length) {
                 int bytesWrite = Math.min(length - offset, SEGMENT_SIZE);
                 out.write(jsonString, offset, bytesWrite);
                 offset += bytesWrite;
             }
             out.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    private void serverRead(char[] jsonString) {
        BufferedReader in;
        int offset = 0;
        int bytesRead;

        try {
            in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            bytesRead = in.read(jsonString, offset, SEGMENT_SIZE);
            while (bytesRead > 0) {
                offset += bytesRead;
                bytesRead = in.read(jsonString, offset, SEGMENT_SIZE);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
