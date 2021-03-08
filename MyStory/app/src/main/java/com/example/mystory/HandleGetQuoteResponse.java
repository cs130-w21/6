package com.example.mystory;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;

public class HandleGetQuoteResponse implements HandleResponse {
    public void handleResponse(Context context, char[] response, int responseLength) {
        String[] quotes = new String[3];
        JSONObject _response = new JSONObject();
        JSONArray jsonArray;

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
            jsonArray = _response.getJSONArray("quote");
            quotes[0] = jsonArray.getString(0);
            quotes[1] = jsonArray.getString(1);
            quotes[2] = jsonArray.getString(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pick a quote");
        builder.setItems(quotes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextInputEditText textBox = ((Activity) context).findViewById(R.id.quote);
                textBox.setText(quotes[which]);
            }
        });
        builder.show();
    }
}
