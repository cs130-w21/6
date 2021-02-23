package com.example.mystory;

import org.json.JSONObject;

public class CreateLoginRequest implements CreateRequest {
    public String createRequest(Object in1, Object in2, Object in3) {
        JSONObject request = new JSONObject();

        try {
            request.put("op", "login");
            request.put("uid", (String) in1);
            request.put("password", (String) in2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return request.toString();
    }
}
