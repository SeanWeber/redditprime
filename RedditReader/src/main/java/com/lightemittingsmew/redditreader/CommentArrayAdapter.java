package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smw on 12/23/13.
 */
public class CommentArrayAdapter extends ArrayAdapter<JSONObject> {
    private Context thisContext;
    private ArrayList<JSONObject> articles;

    public CommentArrayAdapter(Context context, int textViewResourceId,
                               ArrayList<JSONObject> objects) {
        super(context, textViewResourceId, objects);
        thisContext = context;
        articles = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) thisContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_comment, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.textViewComment);
        try {
            textView.setText(articles.get(position).getJSONObject("data").getString("body"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rowView;
    }
}
