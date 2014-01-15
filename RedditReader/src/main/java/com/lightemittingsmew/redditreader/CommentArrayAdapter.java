package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
        LinearLayout buttons = (LinearLayout) convertView.findViewById(R.id.linearLayoutButtons);
        ImageButton upvote = (ImageButton) convertView.findViewById(R.id.imageButtonCommentUpvote);
        ImageButton downvote = (ImageButton) convertView.findViewById(R.id.imageButtonCommentDownvote);
        ImageButton reply = (ImageButton) convertView.findViewById(R.id.imageButtonCommentReply);
        TextView body = (TextView) convertView.findViewById(R.id.textViewComment);
        TextView ups = (TextView) convertView.findViewById(R.id.textViewUps);
        TextView downs = (TextView) convertView.findViewById(R.id.textViewDowns);
        TextView author = (TextView) convertView.findViewById(R.id.textViewAuthor);

        final Comment currentComment = articles.get(position);
        final CommentArrayAdapter adapter = this;

        // Display comment information
        body.setText(currentComment.getBody());
        body.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable
        ups.setText(currentComment.getUps());
        downs.setText(currentComment.getDowns());
        author.setText(currentComment.getAuthor());
        layout.setPadding(16 * currentComment.getReplyLevel(), 0, 0, 0);

        if(currentComment.isCollapsed()) {
            // Hide all child comments
            int nextComment = position + 1;
            while(nextComment < articles.size() &&
                    articles.get(nextComment).getReplyLevel() > currentComment.getReplyLevel()){
                articles.get(nextComment).hide();
                nextComment++;
            }
        } else if(!currentComment.isHidden()) {
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
            buttons.setVisibility(View.GONE);
        } else if(currentComment.isCollapsed()){
            // Only show the top bar
            info.setVisibility(View.VISIBLE);
            body.setVisibility(View.GONE);
            buttons.setVisibility(View.GONE);
        } else {
            // Show everything
            info.setVisibility(View.VISIBLE);
            body.setVisibility(View.VISIBLE);
            buttons.setVisibility(View.VISIBLE);
        }

        View.OnClickListener toggleVisibility = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                currentComment.toggle();
                adapter.notifyDataSetChanged();
            }
        };

        info.setOnClickListener(toggleVisibility);
        body.setOnClickListener(toggleVisibility);

        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentComment.upVote();
            }
        });

        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentComment.downVote();
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }
}