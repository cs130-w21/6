package com.example.mystory;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

public class SocketHandler {
    private static final int SEGMENT_SIZE = 65000;

    public static void writeToSocket(char[] jsonString, Socket socket) {
        int requestLength = jsonString.length;
        int offset = 0;
        int bytesWrite;

        try {
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()), SEGMENT_SIZE);
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

    public static int readFromSocket(char[] jsonString, Socket socket) {
        int offset = 0;
        int bytesRead;
        int bufferSize = jsonString.length;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
