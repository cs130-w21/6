package com.example.mystory;

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
    private final int UPLOAD = 1;
    private final int GET_QUOTE = 2;
    private final int DELETE = 3;
    private final int LOAD = 4;
    private final String SERVER_IP = "";
    private final int SERVER_PORT = -1;
    private final int SEGMENT_SIZE = 1024;
    private final int FILE_SIZE = 650000;
    private final int MAX_FILE_SIZE = FILE_SIZE * 20;

    private String mUserName;
    private String mQuote;
    private String mImage;
    private int mLabel;
    private int mOpCode;
    private Socket mSocket;

    SocketHandler(String userName, String quote, String image, int opCode) {
        mUserName = userName;
        mQuote = quote;
        mImage = image;
        mOpCode = opCode;
        try {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            mSocket = new Socket(serverAddress, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    SocketHandler(String str, int opCode) {
        if (opCode == GET_QUOTE) {
            mImage = str;
        } else if (opCode == LOAD) {
            mUserName = str;
        }
        mOpCode = opCode;
        try {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            mSocket = new Socket(serverAddress, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    SocketHandler(int label, int opCode) {
        mLabel = label;
        mOpCode = opCode;
        try {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            mSocket = new Socket(serverAddress, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char[] handler() {
        char[] temp = new char[1];

        switch (mOpCode) {
            case UPLOAD:

                // TODO: upload (image, quote) to server

                break;
            case GET_QUOTE:

                // TODO: get quote from server

                break;
            case DELETE:

                // TODO: request delete story #<mLabel> in server

                break;
            case LOAD:

                // TODO: load jsonArray (in the format of char[]) of the user's stories

                break;
        }

        return temp;
    }

    private void upload() {

    }

    private void getQuote() {

    }

    private void delete() {

    }

    private char[] load() {
        char[] jsonArray = new char[MAX_FILE_SIZE];


        return jsonArray;
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
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    private char[] serverRead() {
        BufferedReader in;
        char[] jsonString = new char[FILE_SIZE];
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

        return jsonString;
    }
}
