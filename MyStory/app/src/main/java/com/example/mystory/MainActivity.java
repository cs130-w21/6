package com.example.mystory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_GPS = 119;
    private final String URL = "https://api.openweathermap.org/data/2.5/weather";
    private final String API_KEY = "369b1b73d7128a1c8e76e400dfd321ee";
    private final float MIN_DIST = 1000;
    private final long MIN_TIME = 100000;
    private final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private TextView mWeatherText;
    private ImageButton mCameraButton;
    private TextInputEditText mUserName;
    private TextInputEditText mUserPassword;
    private String _mUserName;
    private String _mUserPassword;
    private Button mLoginButton;
    private TextView mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherText = findViewById(R.id.weather_text);
        mCameraButton = findViewById(R.id.camera_button);
        mUserName = findViewById(R.id.login_username);
        mUserPassword = findViewById(R.id.login_password);
        mLoginButton = findViewById(R.id.login);
        mRegisterButton = findViewById(R.id.register);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this,
                        RegistrationActivity.class);
                startActivity(registerIntent);
            }
        });

        mUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                _mUserName = mUserName.getText().toString();
                Log.d("MyStory", "username entered: " + _mUserName);
                return true;
            }
        });

        mUserPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                _mUserPassword = mUserPassword.getText().toString();
                Log.d("MyStory", "password entered: " + _mUserPassword);
                return true;
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: check username & password validity
                // TODO: jump to <StoryActivity> screen

                Intent storyIntent = new Intent(MainActivity.this,
                        StoryActivity.class);
                storyIntent.putExtra("username", _mUserName);
                startActivity(storyIntent);
            }
        });

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] photoOption = {"from gallery", "use camera"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Pick a source");
                builder.setItems(photoOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MyStory", "user clicked option: " + photoOption[which]);
                        Intent photoIntent = new Intent(MainActivity.this,
                                PhotoActivity.class);
                        photoIntent.putExtra("option", which);
                        photoIntent.putExtra("login", false);
                        photoIntent.putExtra("username", "");
                        startActivity(photoIntent);
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MyStory", "homepage onResume() called");
        Log.d("MyStory", "loading weather information");
        getWeather();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    private void getWeather() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("MyStory", "location change detected");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                Log.d("MyStory", "longitude = " + longitude);
                Log.d("MyStory", "latitude = " + latitude);
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", API_KEY);
                params.put("units", "metric");
                updateWeatherText(params);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.d("MyStory", "GPS provider disabled");
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                Log.d("MyStory", "GPS provider enabled");
            }
        };

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_GPS);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER,
                MIN_TIME,
                MIN_DIST,
                mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_GPS && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("MyStory", "GPS access permitted");
        }
    }

    private void updateWeatherText(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("MyStory", "fetching weather data successfully");
                mWeatherText.setText(getWeatherText(response));
                findViewById(R.id.empty_background).setVisibility(View.GONE);
                findViewById(R.id.spinner).setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  String responseString,
                                  Throwable throwable) {
                Log.d("MyAtory", "fecthing weather data failed: " + throwable.toString());
                Toast.makeText(MainActivity.this,
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
            condition = weatherData.getJSONArray("weather").getJSONObject(0).getInt("id");
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
            icon = "weather = 🤷‍♂️";
        }

        return Integer.toString(temperature) + "℃ " + icon;
    }
}