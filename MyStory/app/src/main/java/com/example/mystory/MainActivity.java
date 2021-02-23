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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.RequestParams;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_GPS = 119;
    private final String API_KEY = "369b1b73d7128a1c8e76e400dfd321ee";
    private final float MIN_DIST = 1000;
    private final long MIN_TIME = 100000;
    private final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private ImageButton mCameraButton;
    private TextInputEditText mUserName;
    private TextInputEditText mUserPassword;
    private String _mUserName;
    private String _mUserPassword;
    private Button mLoginButton;
    private TextView mRegisterButton;
    private ImageView mBird;
    private int GPSFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraButton = findViewById(R.id.camera_button);
        mUserName = findViewById(R.id.login_username);
        mUserPassword = findViewById(R.id.login_password);
        mLoginButton = findViewById(R.id.login);
        mRegisterButton = findViewById(R.id.register);
        mBird = findViewById(R.id.bird);
        Glide.with(this).load(R.drawable.bird).into(mBird);

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
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                _mUserName = mUserName.getText().toString();
                Log.d("MyStory", "username entered: " + _mUserName);
                return true;
            }
        });

        mUserPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                _mUserPassword = mUserPassword.getText().toString();
                Log.d("MyStory", "password entered: " + _mUserPassword);
                return true;
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommandSet.addCommand("login",
                        new RegisterCommand(MainActivity.this,
                                new CreateLoginRequest(),
                                new HandleLoginResponse(),
                                _mUserName,
                                _mUserPassword));
                CommandSet.trigger("login");
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
                GetWeather getWeather = new GetWeather(MainActivity.this, params);
                getWeather.updateWeatherText();
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
            if (GPSFlag == 1) {
                findViewById(R.id.empty_background).setVisibility(View.GONE);
                mBird.setVisibility(View.GONE);
                return;
            }
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
        } else {
            Toast.makeText(this,
                    "you may permit GPS later",
                    Toast.LENGTH_SHORT).show();
            GPSFlag = 1;
        }
    }
}