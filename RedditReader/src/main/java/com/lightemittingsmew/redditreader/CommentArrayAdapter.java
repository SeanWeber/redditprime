package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
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

        ArrayList<JSONObject> listReplies = new ArrayList<JSONObject>();
        TextView textView = (TextView) rowView.findViewById(R.id.textViewComment);
        ListView listViewReplies = (ListView)rowView.findViewById(R.id.listViewReplies);

        try {
            JSONObject comment = articles.get(position).getJSONObject("data");
            JSONArray replies = comment.getJSONObject("replies").getJSONObject("data").getJSONArray("children");
            for(int i=0;i<replies.length();i++){
                listReplies.add(replies.getJSONObject(i));
            }
            textView.setText(comment.getString("body"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter commentAdapter = new CommentArrayAdapter(thisContext, R.layout.list_comment, listReplies);
        listViewReplies.setAdapter(commentAdapter);

        return rowView;
    }
}
