package com.example.mystory;

import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

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
    private final String SERVER = "0.tcp.ngrok.io";
    private final int SERVER_PORT = 15972;
    private final int SEGMENT_SIZE = 650000;

    private String mRequest;
    private Socket mSocket;
    private int mLength = -1;

    SocketHandler(String request) {
        mRequest = request;
    }

    public int getLength() {
        return mLength;
    }

    public void handler(char[] jsonString) {
        try {
            new MyTask().execute(mRequest.toCharArray(), jsonString).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("MyStory", "network session done");
    }

    private class MyTask extends AsyncTask<char[], Void, Void> {
        @Override
        protected Void doInBackground(char[]... jsonString) {
            Log.d("MyStory", "setup socket");
            try {
                InetAddress serverAddress = InetAddress.getByName(SERVER);
                mSocket = new Socket(serverAddress, SERVER_PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("MyStory", "socket ready");

            serverWrite(jsonString[0]);
            // return serverRead(jsonString[1]);
            Log.d("MyStory", "manually setting length");
            mLength = 3;
            return null;
        }

        private void serverWrite(char[] jsonString) {
            BufferedWriter out;
            int length = jsonString.length;
            int offset = 0;
            Log.d("MyStory",
                    "first few bytes: " + new String(jsonString, 0, 50));

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

            Log.d("MyStory", "finished sending");
        }

        private void serverRead(char[] jsonString) {
            BufferedReader in;
            int offset = 0;
            int bytesRead;
            Log.d("MyStory", "start reading from socket");

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

            Log.d("MyStory", "finished reading from socket");
            mLength = offset;
        }
    }
}
