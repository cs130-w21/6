package com.example.mystory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayList;

public class StoryActivity extends AppCompatActivity {
    private final int SMALL_FILE_SIZE = 300;
    private final int LARGE_FILE_SIZE = 650000 * 20;
    private final int DELETE = 0;
    private final int LOAD = 1;

    private ListView mStoryListView;
    private ArrayList<JSONObject> mStoryList;
    private StoryListAdapter mAdapter;
    private String mUserName;
    private ImageButton mCameraButton;
    private char[] mJsonString;
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
        mAdapter = new StoryListAdapter(this, mStoryList);
        mStoryListView.setAdapter(mAdapter);
        Log.d("MyStory", "get ready to display <" + mUserName + ">'s stories");

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
                            int label = -1;
                            Log.d("MyStory", "now deleting this story");
                            try {
                                label = mStoryList.get(position).getInt("label");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (workOnNetwork(DELETE, label)) {
                                updateStoryList();
                            }
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
        updateStoryList();
    }

    private boolean workOnNetwork(int requestCode, int label) {
        JSONObject request = new JSONObject();
        int length;

        try {
            if (requestCode == DELETE) {
                request.put("op", "delete");
                request.put("data", label);
            } else if (requestCode == LOAD) {
                request.put("op", "load");
                request.put("data", mUserName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SocketHandler frontEndBackEndChannel = new SocketHandler(request.toString());
        mJsonString = new char[requestCode == DELETE ? SMALL_FILE_SIZE : LARGE_FILE_SIZE];
        frontEndBackEndChannel.handler(mJsonString);
        length = frontEndBackEndChannel.getLength();

        try {
            mResponse = new JSONObject(new String(mJsonString, 0, length));
            if (!mResponse.getBoolean("status")) {
                Toast.makeText(this,
                        "please try again later",
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateStoryList() {
        mStoryList = new ArrayList<>();
        if (!workOnNetwork(LOAD, -1)) {
            return;
        }

        try {
            JSONArray jsonArray = mResponse.getJSONArray("data");
            int len = jsonArray.length();

            for (int i = 0; i < len; i++) {
                mStoryList.add(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("MyStory", "finished loading history");
        mAdapter.notifyDataSetChanged();

        if (mStoryList.size() > 0) {
            findViewById(R.id.empty_story).setVisibility(View.GONE);
            findViewById(R.id.empty_box).setVisibility(View.GONE);
        } else {
            findViewById(R.id.empty_story).setVisibility(View.VISIBLE);
            findViewById(R.id.empty_box).setVisibility(View.VISIBLE);
        }
    }
}