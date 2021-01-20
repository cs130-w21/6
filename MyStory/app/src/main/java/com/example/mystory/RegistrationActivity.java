package com.example.mystory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends AppCompatActivity {
    TextView pagetile;
    TextView name_request;
    TextView pw_request;
    TextView pw_confirm;
    TextView email;
    TextInputEditText UserName_in;
    TextInputEditText UserPassword_in;
    TextInputEditText UserPassword_in2;
    TextInputEditText email_in;
    String UserName;
    String UserPassword;
    String UserPassword2;
    String UserEmail;
    Button create_bt;
    Button login_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // TODO: configure registration screen
        pagetile = findViewById(R.id.title);
        name_request = findViewById(R.id.n_username);
        pw_request = findViewById(R.id.n_password);
        pw_confirm = findViewById(R.id.n_password2);
        email = findViewById(R.id.n_email);
        UserName_in = findViewById(R.id.new_username);
        UserPassword_in = findViewById(R.id.new_password);
        UserPassword_in2 = findViewById(R.id.new_password2);
        email_in = findViewById(R.id.new_email);
        login_bt = findViewById(R.id.login);
        create_bt = findViewById(R.id.create);


        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this,
                        MainActivity.class);
                startActivity(intent);
            }
        });

        create_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: only all validation passed jump to next page
                Intent intent = new Intent(RegistrationActivity.this,
                        MainActivity.class);
                startActivity(intent);
            }
        });

        UserName_in.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                UserName = UserName_in.getText().toString();
                Log.d("MyStory", "username: " + UserName);
                // TODO: check username already exist return false;
                return true;
            }
        });

        UserPassword_in.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                UserPassword = UserPassword_in.getText().toString();
                Log.d("MyStory", "password: " + UserPassword);
                return true;
            }
        });

        UserPassword_in2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                UserPassword2 = UserPassword_in2.getText().toString();
                Log.d("MyStory", "password2: " + UserPassword2);
                if(UserPassword == UserPassword2){
                    return true;
                }else{
                    Log.d("MyStory", "password2 is not equal as password1 ");
                    return false;
                }
            }
        });

        UserPassword_in.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                UserEmail = email_in.getText().toString();
                Log.d("MyStory", "email: " + UserEmail);
                //TODO: check email valid
                return true;
            }
        });

    }
}