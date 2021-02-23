package com.example.mystory;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalTime;
import cz.msebera.android.httpclient.Header;

public class GetWeather {
    private final String URL = "https://api.openweathermap.org/data/2.5/weather";
    private Context mContext;
    private RequestParams mParams;

    public GetWeather(Context context, RequestParams params) {
        mContext = context;
        mParams = params;
    }

    public void updateWeatherText() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, mParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("MyStory", "fetching weather data successfully");
                ((TextView) ((Activity) mContext).findViewById(R.id.weather_text))
                        .setText(getWeatherText(response));
                ((Activity) mContext).findViewById(R.id.empty_background).setVisibility(View.GONE);
                ((Activity) mContext).findViewById(R.id.bird).setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  String responseString,
                                  Throwable throwable) {
                Log.d("MyAtory", "fecthing weather data failed: " + throwable.toString());
                Toast.makeText(mContext,
                        "unable to retrieve your location, please refresh app 😣",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getWeatherText(JSONObject weatherData) {
        int temperature;
        int condition;
        String icon;
        LocalTime now = LocalTime.now();

        try {
            temperature = (int) weatherData.getJSONObject("main").getDouble("temp");
            condition = weatherData.getJSONArray("weather")
                    .getJSONObject(0).getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (condition % 100 == 2) {
            icon = "🌩";
        } else if (condition % 100 == 3) {
            icon = "🌧";
        } else if (condition % 100 == 5) {
            icon = "☔";
        } else if (condition % 100 == 6) {
            icon = "☃️";
        } else if (condition % 100 == 7) {
            icon = "🌫";
        } else if (condition == 800) {
            if (now.getHour() > 7 && now.getHour() < 17) {
                icon = "☀️";
            } else {
                icon = "🌙";
            }
        } else if (condition % 100 == 8) {
            icon = "☁️";
        } else {
            icon = "";
        }

        return Integer.toString(temperature) + "℃ " + icon;
    }
}
