package com.example.mystory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class StoryActivity extends AppCompatActivity {
    private final int SMALL_FILE_SIZE = 100;
    private final int LARGE_FILE_SIZE = 65000 * 20;
    private final int DELETE = 0;
    private final int LOAD = 1;

    private ListView mStoryListView;
    private ArrayList<JSONObject> mStoryList;
    private StoryListAdapter mAdapter;
    private String mUserName;
    private ImageButton mCameraButton;
    private ProgressBar mProgressBar;
    private CardView mProgressBarBackground;
    private int mUserAction;
    private int mPosition;
    private JSONObject mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        mStoryList = new ArrayList<>();
        Intent myIntent = getIntent();
        mUserName = myIntent.getStringExtra("username");
        mStoryListView = findViewById(R.id.story_list);
        mCameraButton = findViewById(R.id.camera_button_2);
        mProgressBar = findViewById(R.id.progress_bar_story_activity);
        mProgressBarBackground = findViewById(R.id.progress_bar_background_2);
        mAdapter = new StoryListAdapter(this, mStoryList);
        mStoryListView.setAdapter(mAdapter);
        Log.d("MyStory", "get ready to display <" + mUserName + ">'s stories");
        mProgressBar.setVisibility(View.GONE);
        mProgressBarBackground.setVisibility(View.GONE);

        mStoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String[] option = {"OK", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(StoryActivity.this);
                builder.setTitle("delete this story ?");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Log.d("MyStory", "now deleting this story");
                            mUserAction = DELETE;
                            mPosition = position;
                            new MyTask().execute();
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] photoOption = {"from gallery", "use camera"};
                AlertDialog.Builder builder = new AlertDialog.Builder(StoryActivity.this);
                builder.setTitle("Pick a source");
                builder.setItems(photoOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent photoIntent = new Intent(StoryActivity.this,
                                PhotoActivity.class);
                        photoIntent.putExtra("option", which);
                        photoIntent.putExtra("login", true);
                        photoIntent.putExtra("username", mUserName);
                        startActivity(photoIntent);
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserAction = LOAD;
        new MyTask().execute();
    }

    private class MyTask extends AsyncTask<Void, Void, Integer> {
        private final String SERVER = "2.tcp.ngrok.io";
        private final int SERVER_PORT = 10864;

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
            JSONObject request = new JSONObject();

            if (mUserAction == DELETE) {
                try {
                    request.put("op", "delete");
                    request.put("row_id",
                            new int[]{mStoryList.get(mPosition).getInt("row_id")});
                    mRequestJsonString = request.toString().toCharArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (mUserAction == LOAD) {
                try {
                    request.put("op", "load");
                    request.put("uid", mUserName);
                    mRequestJsonString = request.toString().toCharArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void updateStoryList() {
            mStoryList.clear();

            try {
                JSONArray jsonArray = mResponse.getJSONArray("data");
                int len = jsonArray.length();

                for (int i = 0; i < len; i++) {
                    mStoryList.add(jsonArray.getJSONObject(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mAdapter.notifyDataSetChanged();

            if (mStoryList.size() > 0) {
                findViewById(R.id.empty_story).setVisibility(View.GONE);
                findViewById(R.id.empty_box).setVisibility(View.GONE);
            } else {
                findViewById(R.id.empty_story).setVisibility(View.VISIBLE);
                findViewById(R.id.empty_box).setVisibility(View.VISIBLE);
            }
        }

        private void handleResponse(int responseLength) {
            try {
                mResponse =
                        new JSONObject(new String(mResponseJsonString, 0, responseLength));
                if (mResponse.getString("op").equals("fail")) {
                    Toast.makeText(StoryActivity.this,
                            "sorry, there was a problem on our side, please try again later",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            updateStoryList();
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

            mResponseJsonString = new char[mUserAction == LOAD ? LARGE_FILE_SIZE : SMALL_FILE_SIZE];
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