package com.example.mystory;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
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
            convertView = inflator.inflate(R.layout.activity_single_story, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.storyImage = (ImageView) convertView.findViewById(R.id.story_image);
            holder.storyText = (TextView) convertView.findViewById(R.id.story_text);
            convertView.setTag(holder);
        }

        JSONObject aStory = getItem(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();

        // TODO: <image> will be changed to String, so we can convert the string back to bitmap

        int image = 0;
        String text = "default text";
        try {
            image = aStory.getInt("image");
            text = aStory.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO: instead of setImageResource, use setImageBitmap (convert Base64 string to bitmap)

        holder.storyImage.setImageResource(image);
        holder.storyText.setText(text);
        return convertView;
    }
}
