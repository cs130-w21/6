package com.example.mystory;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONObject;
import java.util.ArrayList;

public class StoryListAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<JSONObject> mSnapshotList;

    StoryListAdapter(Activity activity, ArrayList<JSONObject> list) {
        mActivity = activity;
        mSnapshotList = list;
    }

    static class ViewHolder {
        ImageView storyImage;
        TextView storyText;
    }

    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return mSnapshotList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflator =
                    (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.activity_single_story,
                    parent,
                    false);
            final ViewHolder holder = new ViewHolder();
            holder.storyImage = (ImageView) convertView.findViewById(R.id.story_image);
            holder.storyText = (TextView) convertView.findViewById(R.id.story_text);
            convertView.setTag(holder);
        }

        JSONObject aStory = getItem(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        byte[] image;
        String text;

        try {
            image = Base64.decode(aStory.getString("image"), Base64.DEFAULT);
            text = aStory.getString("quote");
            holder.storyImage.setImageBitmap(BitmapFactory.decodeByteArray(image,
                    0,
                    image.length));
            holder.storyText.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
