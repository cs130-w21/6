package com.example.mystory;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;
import org.json.JSONObject;
import java.util.ArrayList;

public class DeleteCommand implements Command {
    private Context mContext;
    private StoryListAdapter mAdapter;
    private ArrayList<JSONObject> mStoryList;
    private CreateRequest mRequestMethod;
    private HandleResponse mResponseMethod;
    private int mRowId;
    private String mUserName;
    private ImageView mBird;
    private CardView mProgressBarBackground;

    public DeleteCommand(Context context,
                         StoryListAdapter adapter,
                         ArrayList<JSONObject> list,
                         CreateRequest requestMethod,
                         HandleResponse responseMethod,
                         int rowId,
                         String userName) {
        mContext = context;
        mAdapter = adapter;
        mStoryList = list;
        mRequestMethod = requestMethod;
        mResponseMethod = responseMethod;
        mRowId = rowId;
        mUserName = userName;
        mBird = ((Activity) context).findViewById(R.id.bird3);
        mProgressBarBackground = ((Activity) context).findViewById(R.id.progress_bar_background_2);
    }

    public void setRequestMethod(CreateRequest method) {
        mRequestMethod = method;
    }

    public void setResponseMethod(HandleResponse method) {
        mResponseMethod = method;
    }

    public void myExecute() {
        new MyTask().execute();
    }

    private class MyTask extends AsyncTask<Void, Void, Integer> {
        private String request;
        private char[] response = new char[65000 * 20];

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
            request = mRequestMethod.createRequest(mRowId, mUserName, null);
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
            mAdapter.notifyDataSetChanged();
            if (mStoryList.size() == 0) {
                ((Activity) mContext).findViewById(R.id.empty_box).setVisibility(View.VISIBLE);
                ((Activity) mContext).findViewById(R.id.empty_story).setVisibility(View.VISIBLE);
                ((Activity) mContext).findViewById(R.id.empty_background_2)
                        .setVisibility(View.VISIBLE);
            } else {
                ((Activity) mContext).findViewById(R.id.empty_box).setVisibility(View.GONE);
                ((Activity) mContext).findViewById(R.id.empty_story).setVisibility(View.GONE);
                ((Activity) mContext).findViewById(R.id.empty_background_2)
                        .setVisibility(View.GONE);
            }
        }
    }
}
