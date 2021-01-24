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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayList;

public class StoryActivity extends AppCompatActivity {
    private final int MAX_FILE_SIZE = 650000 * 20;

    private ListView mStoryListView;
    private String mUserName;
    private ImageButton mCameraButton;
    private ArrayList<JSONObject> mStoryList;
    private char[] jsonString = new char[MAX_FILE_SIZE];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        mStoryList = new ArrayList<>();
        Intent myIntent = getIntent();
        mUserName = myIntent.getStringExtra("username");
        mStoryListView = findViewById(R.id.story_list);
        mCameraButton = findViewById(R.id.camera_button_2);
        mStoryListView.setAdapter(new StoryListAdapter(this, mStoryList));
        Log.d("MyStory", "get ready to display <" + mUserName + ">'s stories");

        mStoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String[] option = {"OK"};
                AlertDialog.Builder builder = new AlertDialog.Builder(StoryActivity.this);
                builder.setTitle("delete this story ?");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject request = new JSONObject();
                        Log.d("MyStory", "now deleting this story");

                        // TODO: request server to delete this story

                        try {
                            request.put("op", "delete");
                            request.put("data", mStoryList.get(position).getInt("label"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        SocketHandler frontEndBackEndChannel = new SocketHandler(request, 3);
                        frontEndBackEndChannel.handler(jsonString);
                        updateStoryList();
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

        if (mStoryList.size() > 0) {
            findViewById(R.id.empty_story).setVisibility(View.GONE);
        }
    }

    private void updateStoryList() {
        mStoryList = new ArrayList<>();
        JSONObject request = new JSONObject();

        try {
            request.put("op", "load");
            request.put("data", mUserName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SocketHandler frontEndBackEndChannel = new SocketHandler(request, 4);
        frontEndBackEndChannel.handler(jsonString);

        try {
            JSONObject jsonObject = new JSONObject(new String(jsonString));
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                mStoryList.add(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("MyStory", "finished loading story list");
    }
}