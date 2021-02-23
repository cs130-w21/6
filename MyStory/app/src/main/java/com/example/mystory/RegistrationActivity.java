package com.example.mystory;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

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
                    CommandSet.addCommand("register",
                            new RegisterCommand(RegistrationActivity.this,
                                    new CreateRegisterRequest(),
                                    new HandleRegisterResponse(),
                                    _mNewUserName,
                                    _mNewUserPassword));
                    CommandSet.trigger("register");
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
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                _mNewUserName = mNewUserName.getText().toString();
                Log.d("MyStory", "new username: " + _mNewUserName);
                return true;
            }
        });

        mNewUserPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                _mNewUserPassword = mNewUserPassword.getText().toString();
                Log.d("MyStory", "new password: " + _mNewUserPassword);
                return true;
            }
        });

        mNewUserPassword2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
}