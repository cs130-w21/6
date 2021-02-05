package com.example.mystory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputEditText mNewUserName;
    private TextInputEditText mNewUserPassword;
    private TextInputEditText mNewUserPassword2;
    private String _mNewUserName = "new user";
    private String _mNewUserPassword;
    private String _mNewUserPassword2;
    private Button mCreateButton;
    private Button mBackToHomeButton;


    private ProgressBar mProgressBar;
    private CardView mProgressBarBackground;
    private JSONObject mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mNewUserName = findViewById(R.id.new_username);
        mNewUserPassword = findViewById(R.id.new_password);
        mNewUserPassword2 = findViewById(R.id.new_password2);
        mCreateButton = findViewById(R.id.create);
        mBackToHomeButton = findViewById(R.id.back_to_homepage);

        mProgressBar = findViewById(R.id.progress_bar_story_activity);
        mProgressBarBackground = findViewById(R.id.progress_bar_background_2);
        mProgressBar.setVisibility(View.GONE);
        mProgressBarBackground.setVisibility(View.GONE);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_mNewUserPassword != null && _mNewUserPassword2 != null
                        && _mNewUserPassword.equals(_mNewUserPassword2)) {

                    // TODO: check validity of <_mNewUserName>
                    new RegistrationActivity.MyTaskReg().execute();

                    Intent storyIntent = new Intent(RegistrationActivity.this,
                            StoryActivity.class);
                    storyIntent.putExtra("username", _mNewUserName);
                    startActivity(storyIntent);
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

    private class MyTaskReg extends AsyncTask<Void, Void, Integer> {
        private final String SERVER = "0.tcp.ngrok.io";
        private final int SERVER_PORT = 15972;

        private Socket mSocket;
        private char[] mRequestJsonString;
        private char[] mResponseJsonString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBarBackground.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        private void createRequest() {
            JSONObject mCreateJson = new JSONObject();
            try {
                mCreateJson.put("op", "register");
                mCreateJson.put("UserName", _mNewUserName);
                mCreateJson.put("UserPassword", _mNewUserPassword);
                mRequestJsonString = mCreateJson.toString().toCharArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleResponse(int responseLength) {
            try {
                mResponse =
                        new JSONObject(new String(mResponseJsonString, 0, responseLength));
                if (mResponse.getString("op").equals("fail")) {
                    Toast.makeText(RegistrationActivity.this,
                            "sorry, there was a problem on our side, please try again later",
                            Toast.LENGTH_SHORT).show();
                    return;
                }else if(!mResponse.getString("success").equals("success")) {
                    Toast.makeText(RegistrationActivity.this,
                            "sorry, username exist, please change it",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //updateStoryList();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            //Log.d("MyStory", "setup socket");
            try {
                InetAddress serverAddress = InetAddress.getByName(SERVER);
                //Log.d("MyStory", "server address: " + serverAddress.toString());
                mSocket = new Socket(serverAddress, SERVER_PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.d("MyStory", "socket ready, start sending");

            createRequest();
            SocketHandler.writeToSocket(mRequestJsonString, mSocket);

            //Log.d("MyStory", "finished writing, start reading");

            //mResponseJsonString = new char[mUserAction == LOAD ? LARGE_FILE_SIZE : SMALL_FILE_SIZE];
            return SocketHandler.readFromSocket(mResponseJsonString, mSocket);
        }

        @Override
        protected void onPostExecute(Integer responseLength) {
            super.onPostExecute(responseLength);
            mProgressBar.setVisibility(View.GONE);
            mProgressBarBackground.setVisibility(View.VISIBLE);
            handleResponse(responseLength);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}