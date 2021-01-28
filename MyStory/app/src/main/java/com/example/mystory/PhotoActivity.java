package com.example.mystory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PhotoActivity extends AppCompatActivity {
    private final int REQUEST_CODE_CAMERA = 120;
    private final int FROM_CAMERA = 121;
    private final int FROM_GALLERY = 122;
    private final int SMALL_FILE_SIZE = 300;
    private final int REGULAR_FILE_SIZE = 5000;

    private ImageView mPhoto;
    private TextInputEditText mQuote;
    private String _mQuote = "DEFAULT QUOTE";
    private Button mGetQuoteButton;
    private Button mUploadButton;
    private int mOption;
    private Bitmap mImage;
    private Boolean mLogin;
    private String mUserName = "";
    private char[] mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mPhoto = findViewById(R.id.photo);
        mQuote = findViewById(R.id.quote);
        mGetQuoteButton = findViewById(R.id.get_quote);
        mUploadButton = findViewById(R.id.upload_story);
        Intent myIntent = getIntent();
        mOption = myIntent.getIntExtra("option", 0);
        mLogin = myIntent.getBooleanExtra("login", false);
        mUserName = myIntent.getStringExtra("username");

        if (mOption == 1) {
            setupCamera();
        } else {
            setupGallery();
        }

        mGetQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyStory", "user chooses auto quote generation");
                String[] quotes = new String[3];
                JSONObject request = new JSONObject();

                // TODO: get quote from server

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                mImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                try {
                    request.put("op", "get_quote");
                    request.put("data",
                            Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SocketHandler frontEndBackEndChannel = new SocketHandler(request);
                mJsonString = new char[REGULAR_FILE_SIZE];
                frontEndBackEndChannel.handler(mJsonString);
                Log.d("MyStory", "hear back from server with quotes");

                try {
                    JSONObject jsonObject = new JSONObject(new String(mJsonString));
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    quotes[0] = jsonArray.getJSONObject(0).getString("quote");
                    quotes[1] = jsonArray.getJSONObject(1).getString("quote");
                    quotes[2] = jsonArray.getJSONObject(2).getString("quote");
                } catch (JSONException e) {
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
                    JSONObject request = new JSONObject();
                    JSONObject temp = new JSONObject();

                    // TODO: pass the (username, image, quote) tuple to server using socket
                    // TODO: navigate BACK to story screen (use finish())

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    mImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    try {
                        temp.put("username", mUserName);
                        temp.put("quote", _mQuote);
                        temp.put("image",
                                Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT));
                        request.put("op", "upload");
                        request.put("data", temp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    SocketHandler frontEndBackEndChannel = new SocketHandler(request);
                    mJsonString = new char[SMALL_FILE_SIZE];
                    frontEndBackEndChannel.handler(mJsonString);
                    finish();
                } else {
                    String[] option = {"return to homepage"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this);
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

    private void setupGallery() {
        Intent pickPicture = new Intent(Intent.ACTION_PICK);
        pickPicture.setType("image/*");
        Log.d("MyStory", "pick photo from gallery");
        startActivityForResult(pickPicture, FROM_GALLERY);
    }

    private void setupCamera() {
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
            setupCamera();
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
                mImage = Bitmap.createScaledBitmap(mImage, 300, 300, false);
                mPhoto.setImageBitmap(mImage);
            } else {
                Uri selectedImage = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    mImage = BitmapFactory.decodeStream(imageStream);
                    mImage = Bitmap.createScaledBitmap(mImage, 300, 300, false);
                    mPhoto.setImageBitmap(mImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}