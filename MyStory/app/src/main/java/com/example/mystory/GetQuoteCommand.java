package com.example.mystory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import java.io.ByteArrayOutputStream;

public class GetQuoteCommand implements Command {
    private Bitmap mImage;
    private CreateRequest mRequestMethod;
    private HandleResponse mResponseMethod;
    private Context mContext;
    private ImageView mBird;
    private CardView mProgressBarBackground;

    public GetQuoteCommand(Context context,
                           CreateRequest requestMethod,
                           HandleResponse responseMethod,
                           Bitmap image) {
        mImage = image;
        mRequestMethod = requestMethod;
        mResponseMethod = responseMethod;
        mContext = context;
        mBird = ((Activity) context).findViewById(R.id.bird2);
        mProgressBarBackground = ((Activity) context).findViewById(R.id.progress_bar_background);
        Log.d("MyStory", "user chooses auto quote generation");
    }

    public void setRequestMethod(CreateRequest method) {
        mRequestMethod = method;
    }

    public void setResponseMethod(HandleResponse method) {
        mResponseMethod = method;
    }

    public void myExecute() {
        if (mImage == null) {
            Toast.makeText(mContext,
                    "need a photo here, please navigate back to previous screen",
                    Toast.LENGTH_SHORT).show();
        } else {
            new MyTask().execute();
        }
    }

    private class MyTask extends AsyncTask<Void, Void, Integer> {
        private String request;
        private char[] response = new char[5000];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBird.setVisibility(View.VISIBLE);
            mProgressBarBackground.setVisibility(View.VISIBLE);
            ((Activity) mContext).getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            mImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            request = mRequestMethod.createRequest(
                    Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT),
                    null,
                    null);

            SocketHandler socketHandler = new SocketHandler();
            socketHandler.writeToSocket(request.toCharArray());
            return socketHandler.readFromSocket(response);
        }

        @Override
        protected void onPostExecute(Integer responseLength) {
            super.onPostExecute(responseLength);
            mBird.setVisibility(View.GONE);
            mProgressBarBackground.setVisibility(View.GONE);
            ((Activity) mContext).getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mResponseMethod.handleResponse(mContext, response, responseLength);
        }
    }
}
