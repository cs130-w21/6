package com.example.mystory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoryActivity extends AppCompatActivity {
    private ListView mStoryListView;
    private String mUserName;
    private ImageButton mCameraButton;
    private ArrayList<JSONObject> mStoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        Log.d("MyStory", "now at story screen");
        mStoryList = new ArrayList<>();
        JSONObject temp = new JSONObject();
        try {
            temp.put("image",
                    getResources().getIdentifier("camera_logo",
                    "drawable",
                            getPackageName()));
            temp.put("text", "DEFAULT TEXT");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mStoryList.add(temp);
        mStoryList.add(temp);
        mStoryList.add(temp);
        mStoryList.add(temp);
        mStoryList.add(temp);
        mStoryList.add(temp);
        mStoryList.add(temp);
        mStoryList.add(temp);
        mStoryList.add(temp);
        mStoryList.add(temp);

        Intent myIntent = getIntent();
        mUserName = myIntent.getStringExtra("username");
        Log.d("MyStory", "get ready to display <" + mUserName + ">'s stories");

        // TODO: request this user's story history list from server

        mStoryListView = findViewById(R.id.story_list);
        mStoryListView.setAdapter(new StoryListAdapter(this, mStoryList));

        // TODO: mStoryListView.setOnItemClickListener(),
        //  so user can choose whether to delete this story

        mCameraButton = findViewById(R.id.camera_button_2);
        String[] photoOption = {"from gallery", "use camera"};
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StoryActivity.this);
                builder.setTitle("Pick a source");
                builder.setItems(photoOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent photoIntent = new Intent(StoryActivity.this,
                                PhotoActivity.class);
                        photoIntent.putExtra("option", which);
                        photoIntent.putExtra("login", true);
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

        // TODO: update <mStoryList> from server

    }
}