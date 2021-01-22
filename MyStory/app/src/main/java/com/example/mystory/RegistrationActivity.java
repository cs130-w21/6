package com.example.mystory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputEditText mNewUserName;
    private TextInputEditText mNewUserPassword;
    private TextInputEditText mNewUserPassword2;
    private String _mNewUserName;
    private String _mNewUserPassword;
    private String _mNewUserPassword2;
    private Button mCreateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mNewUserName = findViewById(R.id.new_username);
        mNewUserPassword = findViewById(R.id.new_password);
        mNewUserPassword2 = findViewById(R.id.new_password2);
        mCreateButton = findViewById(R.id.create);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_mNewUserPassword != null && _mNewUserPassword2 != null
                        && _mNewUserPassword.equals(_mNewUserPassword2)) {

                    // TODO: check validity of <_mNewUserName>

                    Intent storyIntent = new Intent(RegistrationActivity.this,
                            StoryActivity.class);
                    storyIntent.putExtra("username", _mNewUserName);
                    startActivity(storyIntent);
                } else {
                    Toast.makeText(RegistrationActivity.this,
                            "please make sure that password match",
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
    }
}