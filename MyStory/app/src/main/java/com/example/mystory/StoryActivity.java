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
        Intent myIntent = getIntent();
        mUserName = myIntent.getStringExtra("username");
        Log.d("MyStory", "get ready to display <" + mUserName + ">'s stories");

        mStoryListView = findViewById(R.id.story_list);
        mStoryListView.setAdapter(new StoryListAdapter(this, mStoryList));

        String[] option = {"OK"};
        mStoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StoryActivity.this);
                builder.setTitle("delete this story ?");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MyStory", "noe deleting this story");

                        // TODO: request server to delete this story

                    }
                });
                builder.show();
                return true;
            }
        });

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

        // TODO: load <mStoryList> from server

        if (mStoryList.size() > 0) {
            findViewById(R.id.empty_story).setVisibility(View.GONE);
        }
    }
}