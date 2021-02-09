package com.example.mystory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.Socket;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputEditText mNewUserName;
    private TextInputEditText mNewUserPassword;
    private TextInputEditText mNewUserPassword2;
    private String _mNewUserName = "new user";
    private String _mNewUserPassword;
    private String _mNewUserPassword2;
    private Button mCreateButton;
    private Button mBackToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mNewUserName = findViewById(R.id.new_username);
        mNewUserPassword = findViewById(R.id.new_password);
        mNewUserPassword2 = findViewById(R.id.new_password2);
        mCreateButton = findViewById(R.id.create);
        mBackToHomeButton = findViewById(R.id.back_to_homepage);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_mNewUserPassword != null && _mNewUserPassword2 != null
                        && _mNewUserPassword.equals(_mNewUserPassword2)) {
                    new MyTask().execute();
                } else {
                    Toast.makeText(RegistrationActivity.this,
                            "please make sure password non-empty & match",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mNewUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                _mNewUserName = mNewUserName.getText().toString();
                Log.d("MyStory", "new username: " + _mNewUserName);
                return true;
            }
        });

        mNewUserPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                _mNewUserPassword = mNewUserPassword.getText().toString();
                Log.d("MyStory", "new password: " + _mNewUserPassword);
                return true;
            }
        });

        mNewUserPassword2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                _mNewUserPassword2 = mNewUserPassword2.getText().toString();
                Log.d("MyStory", "new confirmed password: " + _mNewUserPassword2);
                return true;
            }
        });

        mBackToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class MyTask extends AsyncTask<Void, Void, Integer>{
        private final String SERVER = "2.tcp.ngrok.io";
        private final int SERVER_PORT = 10864;

        private Socket mSocket;
        private char[] mRequestJsonString;
        private char[] mResponseJsonString= new char[100];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            JSONObject request = new JSONObject();

            try {
                InetAddress serverAddress = InetAddress.getByName(SERVER);
                mSocket = new Socket(serverAddress, SERVER_PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                request.put("op", "register");
                request.put("uid", _mNewUserName);
                request.put("password", _mNewUserPassword);
                mRequestJsonString = request.toString().toCharArray();
            } catch (Exception e) {
                e.printStackTrace();
            }

            SocketHandler.writeToSocket(mRequestJsonString, mSocket);
            return SocketHandler.readFromSocket(mResponseJsonString, mSocket);
        }

        @Override
        protected void onPostExecute(Integer responseLength) {
            super.onPostExecute(responseLength);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            JSONObject response;

            try {
                response = new JSONObject(new String(mResponseJsonString, 0, responseLength));
                if (response.getString("op").equals("fail")) {
                    Toast.makeText(RegistrationActivity.this,
                            "Something went wrong on our side, please try again later",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (response.getInt("success") == 1) {
                        Intent storyIntent = new Intent(RegistrationActivity.this,
                                StoryActivity.class);
                        storyIntent.putExtra("username", _mNewUserName);
                        startActivity(storyIntent);
                    } else {
                        Toast.makeText(RegistrationActivity.this,
                                "username is already taken",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}