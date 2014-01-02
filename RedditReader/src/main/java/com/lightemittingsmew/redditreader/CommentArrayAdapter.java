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

    public CommentArrayAdapter(Context context, int textViewResourceId, ArrayList<Comment> objects){
        super(context, textViewResourceId, objects);
        thisContext = context;
        articles = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment currentComment = articles.get(position);
        ArrayList<Comment> listReplies;

        // Get the proper views and layouts
        LayoutInflater inflater = (LayoutInflater) thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_comment, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewComment);
        LinearLayout replies = (LinearLayout) rowView.findViewById(R.id.replies);

        // Display comment information
        textView.setText(currentComment.getBody());
        listReplies = currentComment.getReplies();

        // Show any replies
        if(listReplies != null){
            for(Comment reply : listReplies){
                View line = inflater.inflate(R.layout.list_comment, null);

                TextView tv = (TextView) line.findViewById(R.id.textViewComment);
                tv.setText(reply.getBody());

                replies.addView(line);
            }
        }

        return rowView;
    }
}