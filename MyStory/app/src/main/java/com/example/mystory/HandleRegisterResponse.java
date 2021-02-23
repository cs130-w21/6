package com.example.mystory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONObject;

public class HandleRegisterResponse implements HandleResponse {
    public void handleResponse(Context context, char[] response, int responseLength) {
        JSONObject _response = null;

        try {
            _response = new JSONObject(new String(response, 0, responseLength));
            if (_response.getString("op").equals("fail")) {
                Toast.makeText(context,
                        "something went wrong on our side, please try again later",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (_response.getInt("success") == 0) {
                Toast.makeText(context, "username already taken", Toast.LENGTH_SHORT).show();
            } else {
                Intent storyIntent = new Intent(context,
                        StoryActivity.class);
                storyIntent.putExtra("username", _response.getString("uid"));
                context.startActivity(storyIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
