package com.example.mystory;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import org.json.JSONObject;

public class HandleUploadResponse implements HandleResponse {
    public void handleResponse(Context context, char[] response, int responseLength) {
        JSONObject _response;

        try {
            _response = new JSONObject(new String(response, 0, responseLength));
            if (_response.getString("op").equals("fail")) {
                Toast.makeText(context,
                        "something went wrong on our side, please try again later",
                        Toast.LENGTH_SHORT).show();
            } else {
                ((Activity) context).finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
