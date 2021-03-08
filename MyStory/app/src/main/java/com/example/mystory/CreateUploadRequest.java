package com.example.mystory;

import org.json.JSONObject;

public class CreateUploadRequest implements CreateRequest {
    public String createRequest(Object in1, Object in2, Object in3) {
        JSONObject request = new JSONObject();

        try {
            request.put("op", "confirm");
            request.put("uid", (String) in1);
            request.put("image", (String) in2);
            request.put("quote", (String) in3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return request.toString();
    }
}
