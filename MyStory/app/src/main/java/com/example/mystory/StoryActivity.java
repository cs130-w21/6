package com.example.mystory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class StoryActivity extends AppCompatActivity {
    private ListView mStoryListView;
    private ArrayList<JSONObject> mStoryList;
    private StoryListAdapter mAdapter;
    private String mUserName;
    private ImageButton mCameraButton;
    private CardView mProgressBarBackground;
    private ImageView mBird;
    private CardView mCard;
    private ImageView mCardImage;
    private TextView mCardText;
    private TextToSpeech mSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        mStoryList = new ArrayList<>();
        Intent myIntent = getIntent();
        mUserName = myIntent.getStringExtra("username");
        mStoryListView = findViewById(R.id.story_list);
        mCameraButton = findViewById(R.id.camera_button_2);
        mBird = findViewById(R.id.bird3);
        Glide.with(this).load(R.drawable.bird).into(mBird);
        mProgressBarBackground = findViewById(R.id.progress_bar_background_2);
        mCard = findViewById(R.id.story_card);
        mCardImage = findViewById(R.id.story_card_image);
        mCardText = findViewById(R.id.story_card_text);
        mAdapter = new StoryListAdapter(this, mStoryList);
        mStoryListView.setAdapter(mAdapter);
        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
        Log.d("MyStory", "get ready to display <" + mUserName + ">'s stories");
        mBird.setVisibility(View.GONE);
        mProgressBarBackground.setVisibility(View.GONE);
        findViewById(R.id.empty_story).setVisibility(View.GONE);
        findViewById(R.id.empty_box).setVisibility(View.GONE);
        findViewById(R.id.empty_background_2).setVisibility(View.GONE);
        mCard.setVisibility(View.GONE);

        mStoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String[] option = {"delete this story", "view this story"};
                AlertDialog.Builder builder = new AlertDialog.Builder(StoryActivity.this);
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Log.d("MyStory", "now deleting this story");
                            int idx = 0;
                            try {
                                idx = mStoryList.get(position).getInt("row_id");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            CommandSet.addCommand("delete",
                                    new DeleteCommand(StoryActivity.this,
                                            mAdapter,
                                            mStoryList,
                                            new CreateDeleteRequest(),
                                            new HandleLoadResponse(mStoryList),
                                            idx,
                                            mUserName));
                            CommandSet.trigger("delete");
                        } else {
                            try {
                                byte[] image = Base64.decode(mStoryList.get(position)
                                        .getString("image"), Base64.DEFAULT);
                                String text = mStoryList.get(position).getString("quote");
                                mCard.setVisibility(View.VISIBLE);
                                mCardImage.setImageBitmap(BitmapFactory.decodeByteArray(image,
                                        0,
                                        image.length));
                                mCardText.setText(text);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        mStoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mSpeech.speak(mStoryList.get(position).getString("quote"),
                            TextToSpeech.QUEUE_FLUSH,
                            null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCard.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommandSet.addCommand("load",
                new LoadCommand(this,
                        mAdapter,
                        mStoryList,
                        new CreateLoadRequest(),
                        new HandleLoadResponse(mStoryList),
                        mUserName));
        CommandSet.trigger("load");
    }
}