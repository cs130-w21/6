package com.example.mystory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class PhotoActivity extends AppCompatActivity {
    private final int REQUEST_CODE_CAMERA = 120;
    private final int REQUEST_CODE_INTERNET = 121;
    private final int GET_QUOTE = 122;
    private final int UPLOAD_STORY = 123;
    private final int FROM_CAMERA = 124;
    private final int FROM_GALLERY = 125;
    private final int SMALL_FILE_SIZE = 100;
    private final int REGULAR_FILE_SIZE = 1000;

    private ImageView mPhoto;
    private TextInputEditText mQuote;
    private String _mQuote = "DEFAULT QUOTE";
    private Button mGetQuoteButton;
    private Button mUploadButton;
    private ProgressBar mProgressBar;
    private CardView mProgressBarBackground;
    private int mPhotoOption;
    private Bitmap mImage;
    private Boolean mLogin;
    private String mUserName;
    private int mUserAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mPhoto = findViewById(R.id.photo);
        mQuote = findViewById(R.id.quote);
        mGetQuoteButton = findViewById(R.id.get_quote);
        mUploadButton = findViewById(R.id.upload_story);
        mProgressBar = findViewById(R.id.progress_bar_photo_activity);
        mProgressBarBackground = findViewById(R.id.progress_bar_background);
        Intent myIntent = getIntent();
        mPhotoOption = myIntent.getIntExtra("option", 0);
        mLogin = myIntent.getBooleanExtra("login", false);
        mUserName = myIntent.getStringExtra("username");
        mProgressBar.setVisibility(View.GONE);
        mProgressBarBackground.setVisibility(View.GONE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_CODE_INTERNET);
        }

        if (mPhotoOption == 1) {
            workOnCamera();
        } else {
            workOnGallery();
        }

        mGetQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyStory", "user chooses auto quote generation");
                if (mImage == null) {
                    Toast.makeText(PhotoActivity.this, "" +
                            "need a photo here, please navigate back to previous screen",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mUserAction = GET_QUOTE;
                    new MyTask().execute();
                }
            }
        });

        mQuote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                _mQuote = mQuote.getText().toString();
                Log.d("MyStory", "user's finalized quote: " + _mQuote);
                return true;
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLogin) {
                    if (mImage == null) {
                        Toast.makeText(PhotoActivity.this,
                                "need a photo here, please navigate back to previous screen",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mUserAction = UPLOAD_STORY;
                        new MyTask().execute();
                    }
                } else {
                    String[] option = {"return to homepage"};
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(PhotoActivity.this);
                    builder.setTitle("Please login first to upload your story 😉");
                    builder.setItems(option, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("MyStory", "user navigate back to homescreen");
                            finish();
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    private void workOnGallery() {
        Intent pickPicture = new Intent(Intent.ACTION_PICK);
        pickPicture.setType("image/*");
        Log.d("MyStory", "pick photo from gallery");
        startActivityForResult(pickPicture, FROM_GALLERY);
    }

    private void workOnCamera() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA);
            return;
        }
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("MyStory", "take picture from camera");
        startActivityForResult(takePicture, FROM_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("MyStory", "camera access granted");
            workOnCamera();
        } else if (requestCode == REQUEST_CODE_INTERNET && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("MyStory", "internet access granted");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("MyStory", "now prepare to display image");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Log.d("MyStory", "display image");
            if (requestCode == FROM_CAMERA) {
                mImage = (Bitmap) data.getExtras().get("data");
                mImage = Bitmap.createScaledBitmap(mImage, 500, 500, false);
                mPhoto.setImageBitmap(mImage);
            } else {
                Uri selectedImage = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    mImage = BitmapFactory.decodeStream(imageStream);
                    mImage = Bitmap.createScaledBitmap(mImage,
                            500,
                            500,
                            false);
                    mPhoto.setImageBitmap(mImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MyTask extends AsyncTask<Void, Void, Integer> {
        private final String SERVER = "2.tcp.ngrok.io";
        private final int SERVER_PORT = 10864;

        private Socket mSocket;
        private char[] mRequestJsonString;
        private char[] mResponseJsonString;
        private JSONObject mResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBarBackground.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        private void createRequest() {
            JSONObject request = new JSONObject();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            mImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            if (mUserAction == GET_QUOTE) {
                try {
                    request.put("op", "upload");
                    request.put("image",
                            Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT));
                    mRequestJsonString = request.toString().toCharArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (mUserAction == UPLOAD_STORY) {
                try {
                    request.put("op", "confirm");
                    request.put("uid", mUserName);
                    request.put("image",
                            Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT));
                    request.put("quote", _mQuote);
                    mRequestJsonString = request.toString().toCharArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleResponse(int responseLength) {
            String[] quotes = new String[3];
            JSONArray jsonArray;

            try {
                mResponse =
                        new JSONObject(new String(mResponseJsonString, 0, responseLength));
                if (mResponse.getString("op").equals("fail")) {
                    Toast.makeText(PhotoActivity.this,
                            "sorry, there was a problem on our side, please try again later",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mUserAction == GET_QUOTE) {
                try {
                    jsonArray = mResponse.getJSONArray("quote");
                    quotes[0] = jsonArray.getString(0);
                    quotes[1] = jsonArray.getString(1);
                    quotes[2] = jsonArray.getString(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this);
                builder.setTitle("Pick a quote");
                builder.setItems(quotes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _mQuote = quotes[which];
                        mQuote.setText(_mQuote);
                    }
                });
                builder.show();
            } else if (mUserAction == UPLOAD_STORY) {
                finish();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Log.d("MyStory", "setup socket");
            try {
                InetAddress serverAddress = InetAddress.getByName(SERVER);
                Log.d("MyStory", "server address: " + serverAddress.toString());
                mSocket = new Socket(serverAddress, SERVER_PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("MyStory", "socket ready, start sending");

            createRequest();
            SocketHandler.writeToSocket(mRequestJsonString, mSocket);

            Log.d("MyStory", "finished writing, start reading");

            mResponseJsonString =
                    new char[mUserAction == GET_QUOTE ? REGULAR_FILE_SIZE : SMALL_FILE_SIZE];
            return SocketHandler.readFromSocket(mResponseJsonString, mSocket);
        }

        @Override
        protected void onPostExecute(Integer responseLength) {
            super.onPostExecute(responseLength);
            mProgressBar.setVisibility(View.GONE);
            mProgressBarBackground.setVisibility(View.GONE);
            handleResponse(responseLength);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}