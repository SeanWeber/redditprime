package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by smw on 12/23/13.
 */
public class CommentArrayAdapter extends ArrayAdapter<Comment> {
    private Context thisContext;
    private ArrayList<Comment> articles;
    LayoutInflater inflater;

    public CommentArrayAdapter(Context context, int textViewResourceId, ArrayList<Comment> objects){
        super(context, textViewResourceId, objects);
        thisContext = context;
        articles = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the proper views and layouts
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_comment, parent, false);
        }

        Comment currentComment = articles.get(position);

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.linearLayoutComment);
        TextView body = (TextView) convertView.findViewById(R.id.textViewComment);
        TextView ups = (TextView) convertView.findViewById(R.id.textViewUps);
        TextView downs = (TextView) convertView.findViewById(R.id.textViewDowns);
        TextView author = (TextView) convertView.findViewById(R.id.textViewAuthor);

        // Display comment information
        body.setText(currentComment.getBody());
        ups.setText(currentComment.getUps());
        downs.setText(currentComment.getDowns());
        author.setText(currentComment.getAuthor());
        layout.setPadding(10 * currentComment.getReplyLevel(), 0, 0, 0);

        return convertView;
    }
}