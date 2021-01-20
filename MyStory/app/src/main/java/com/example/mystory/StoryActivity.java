package com.example.mystory;

import androidx.appcompat.app.AppCompatActivity;

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
    ListView mStoryListView;
    String mUserName;
    ImageButton mCameraButton_2;
    ArrayList<JSONObject> mStoryList;

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

        mCameraButton_2 = findViewById(R.id.camera_button_2);
        mCameraButton_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: raise AlertDialogue & navigate to photo screen

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: update <mStoryList> from server

    }
}