package com.example.mystory;

import android.content.Context;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class HandleLoadResponse implements HandleResponse {
    private JSONObject _response;
    private ArrayList<JSONObject> mStoryList;

    public HandleLoadResponse(ArrayList<JSONObject> list) {
        mStoryList = list;
    }

    public void handleResponse(Context context, char[] response, int responseLength) {
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

        updateStoryList();
    }

    private void updateStoryList() {
        mStoryList.clear();

        try {
            JSONArray jsonArray = _response.getJSONArray("data");
            int len = jsonArray.length();

            for (int i = 0; i < len; i++) {
                mStoryList.add(jsonArray.getJSONObject(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
