package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.linearLayoutComment);
        LinearLayout info = (LinearLayout) convertView.findViewById(R.id.linearLayoutCommentInfo);
        TextView body = (TextView) convertView.findViewById(R.id.textViewComment);
        TextView ups = (TextView) convertView.findViewById(R.id.textViewUps);
        TextView downs = (TextView) convertView.findViewById(R.id.textViewDowns);
        TextView author = (TextView) convertView.findViewById(R.id.textViewAuthor);

        Comment currentComment = articles.get(position);

        // Display comment information
        body.setText(Html.fromHtml(currentComment.getBody()));
        //body.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable
        ups.setText(currentComment.getUps());
        downs.setText(currentComment.getDowns());
        author.setText(currentComment.getAuthor());
        layout.setPadding(16 * currentComment.getReplyLevel(), 0, 0, 0);

        if(currentComment.isCollapsed()) {
            body.setVisibility(View.GONE);

            // Hide all child comments
            int nextComment = position + 1;
            while(nextComment < articles.size() &&
                    articles.get(nextComment).getReplyLevel() > currentComment.getReplyLevel()){
                articles.get(nextComment).hide();
                nextComment++;
            }
        } else if(!currentComment.isHidden()) {
            body.setVisibility(View.VISIBLE);

            // Show all child comments
            int nextComment = position + 1;
            while(nextComment < articles.size() &&
                    articles.get(nextComment).getReplyLevel() > currentComment.getReplyLevel()){
                articles.get(nextComment).unhide();
                nextComment++;
            }
        }

        if(currentComment.isHidden()){
            // Hide the entire comment
            info.setVisibility(View.GONE);
            body.setVisibility(View.GONE);
        } else if(currentComment.isCollapsed()){
            // Only hide the body
            info.setVisibility(View.VISIBLE);
            body.setVisibility(View.GONE);
        } else {
            // Show everything
            info.setVisibility(View.VISIBLE);
            body.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}