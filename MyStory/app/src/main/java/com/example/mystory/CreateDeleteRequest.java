package com.example.mystory;

import org.json.JSONArray;
import org.json.JSONObject;

public class CreateDeleteRequest implements CreateRequest {
    public String createRequest(Object in1, Object in2, Object in3) {
        JSONObject request = new JSONObject();
        JSONArray temp = new JSONArray();

        try {
            temp.put(((Integer) in1).intValue());
            request.put("op", "delete");
            request.put("row_id", temp);
            request.put("uid", (String) in2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return request.toString();
    }
}
