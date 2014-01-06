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
    View line;
    LayoutInflater inflater;

    public CommentArrayAdapter(Context context, int textViewResourceId, ArrayList<Comment> objects){
        super(context, textViewResourceId, objects);
        thisContext = context;
        articles = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment currentComment = articles.get(position);
        ArrayList<Comment> listReplies;

        // Get the proper views and layouts
        View rowView = convertView;
        if (rowView == null) {
         rowView = inflater.inflate(R.layout.list_comment, parent, false);
        }

        LinearLayout replies = setComment(currentComment, null, rowView);

        listReplies = currentComment.getReplies();
        parseReplies(listReplies, replies, null);

        return rowView;
    }

    public void parseReplies(ArrayList<Comment> listReplies, LinearLayout replies, View convertView){
        // Show any replies
        if(listReplies != null){
            for(Comment reply : listReplies){

                LinearLayout moreReplies = setComment(reply, replies, convertView);

                // Replies all the way down
                ArrayList<Comment> replyReplies = reply.getReplies();

                // Continue down the reply chain
                parseReplies(replyReplies, moreReplies, convertView);
            }
        }
    }

    public LinearLayout setComment(Comment comment, LinearLayout replies, View convertView){
        // Get the views we need
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_comment, null);
        }
        LinearLayout moreReplies = (LinearLayout) view.findViewById(R.id.replies);
        TextView body = (TextView) view.findViewById(R.id.textViewComment);
        TextView ups = (TextView) view.findViewById(R.id.textViewUps);
        TextView downs = (TextView) view.findViewById(R.id.textViewDowns);
        TextView author = (TextView) view.findViewById(R.id.textViewAuthor);

        // Display comment information
        body.setText(comment.getBody());
        ups.setText(comment.getUps());
        downs.setText(comment.getDowns());
        author.setText(comment.getAuthor());

        if(replies != null){
            replies.addView(view);
        }

        return moreReplies;
    }
}