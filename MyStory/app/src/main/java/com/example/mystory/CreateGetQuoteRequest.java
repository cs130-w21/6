package com.example.mystory;

import org.json.JSONObject;

public class CreateGetQuoteRequest implements CreateRequest {
    public String createRequest(Object in1, Object in2, Object in3) {
        JSONObject request = new JSONObject();

        try {
            request.put("op", "upload");
            request.put("image", (String) in1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return request.toString();
    }
}
