package com.example.mystory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PhotoActivity extends AppCompatActivity {
    private final int REQUEST_CODE_CAMERA = 120;
    private final int FROM_CAMERA = 124;
    private final int FROM_GALLERY = 125;

    private ImageView mPhoto;
    private TextInputEditText mQuote;
    private String _mQuote = "DEFAULT QUOTE";
    private Button mGetQuoteButton;
    private Button mUploadButton;
    private CardView mProgressBarBackground;
    private int mPhotoOption;
    private Bitmap mImage;
    private Boolean mLogin;
    private String mUserName;
    private ImageView mBird;

    /**
     * Specifies the actions when users want to select a picture, get quote, and upload
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mPhoto = findViewById(R.id.photo);
        mQuote = findViewById(R.id.quote);
        mGetQuoteButton = findViewById(R.id.get_quote);
        mUploadButton = findViewById(R.id.upload_story);
        mProgressBarBackground = findViewById(R.id.progress_bar_background);
        mBird = findViewById(R.id.bird2);
        Intent myIntent = getIntent();
        mPhotoOption = myIntent.getIntExtra("option", 0);
        mLogin = myIntent.getBooleanExtra("login", false);
        mUserName = myIntent.getStringExtra("username");
        mBird.setVisibility(View.GONE);
        mProgressBarBackground.setVisibility(View.GONE);
        mQuote.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mQuote.setRawInputType(InputType.TYPE_CLASS_TEXT);
        Glide.with(this).load(R.drawable.bird).into(mBird);

        if (mPhotoOption == 1) {
            workOnCamera();
        } else {
            workOnGallery();
        }

        mGetQuoteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Request a quote from the server based on the selected image
             * when the button is pressed
             * @param v
             */
            @Override
            public void onClick(View v) {
                CommandSet.addCommand("get_quote",
                        new GetQuoteCommand(PhotoActivity.this,
                                new CreateGetQuoteRequest(),
                                new HandleGetQuoteResponse(),
                                mImage));
                CommandSet.trigger("get_quote");
            }
        });

        mQuote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            /**
             * Store and display the generated or user-edited quote
             * @param v
             * @param actionId
             * @param event
             * @return
             */
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                _mQuote = mQuote.getText().toString();
                Log.d("MyStory", "user's finalized quote: " + _mQuote);
                return true;
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Upload the picture and its corresponding quote to the server
             * when the upload button is pressed only when the user is logged in.
             * @param v
             */
            @Override
            public void onClick(View v) {
                if (mLogin) {
                    if (mQuote.getText() == null || mImage == null) {
                        Toast.makeText(PhotoActivity.this,
                                "need an image & caption",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    CommandSet.addCommand("upload",
                            new UploadCommand(PhotoActivity.this,
                                    new CreateUploadRequest(),
                                    new HandleUploadResponse(),
                                    mImage,
                                    mUserName,
                                    mQuote.getText().toString()));
                    CommandSet.trigger("upload");
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

    /**
     * Open gallery and select a picture
     */
    private void workOnGallery() {
        Intent pickPicture = new Intent(Intent.ACTION_PICK);
        pickPicture.setType("image/*");
        Log.d("MyStory", "pick photo from gallery");
        startActivityForResult(pickPicture, FROM_GALLERY);
    }

    /**
     * Open camera and take a picture
     */
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

    /**
     * Request camera access permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("MyStory", "camera access granted");
            workOnCamera();
        }
    }

    /**
     * Convert images in bitmap format to uri format
     * @param inContext
     * @param inImage
     * @return
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Crop the image in bitmap format and save the result as uri.
     * @param mImage
     */
    public void cropImage(Bitmap mImage) {
        CropImage.activity(getImageUri(this, mImage))
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    /**
     * Depending on the request, crop the image from camera/gallery or display the cropped image
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("MyStory", "now prepare to display image");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Log.d("MyStory", "display image");
            if (requestCode == FROM_CAMERA) {
                mImage = (Bitmap) data.getExtras().get("data");
                cropImage(mImage);
            } else if (requestCode == FROM_GALLERY) {
                Uri selectedImage = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    mImage = BitmapFactory.decodeStream(imageStream);
                    cropImage(mImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    mImage = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), result.getUri());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPhoto.setImageBitmap(mImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}