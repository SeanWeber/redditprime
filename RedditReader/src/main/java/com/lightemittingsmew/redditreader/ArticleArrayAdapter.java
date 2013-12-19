package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smw on 12/19/13.
 */
public class ArticleArrayAdapter extends ArrayAdapter<JSONObject> {
    private Context thisContext;
    private ArrayList<JSONObject> articles;

    public ArticleArrayAdapter(Context context, int textViewResourceId,
                               ArrayList<JSONObject> objects) {
        super(context, textViewResourceId, objects);
        thisContext = context;
        articles = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) thisContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_article, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewTitle);

        String title;
        try {
            title = articles.get(position).getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
            title = ":(";
        }
        textView.setText(title);

        return rowView;
    }
}
