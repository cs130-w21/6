package com.example.mystory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SocketHandler {
    final int UPLOAD = 1;
    final int GET_QUOTE = 2;
    final int DELETE = 3;
    final int LOAD = 4;
    final String SERVER_IP = "";
    final int SERVER_PORT = -1;

    private String mUserName;
    private String mQuote;
    private String mImage;
    private int mLable;
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

    SocketHandler(int lable, int opCode) {
        mLable = lable;
        mOpCode = opCode;
        try {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            mSocket = new Socket(serverAddress, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
