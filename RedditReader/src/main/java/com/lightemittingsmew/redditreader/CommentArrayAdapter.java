package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by smw on 12/23/13.
 */
public class CommentArrayAdapter extends ArrayAdapter<Comment> {
    public static final String PARENT_FULLNAME = "com.lightemittingsmew.redditreader.PARENT_FULLNAME";
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
        LinearLayout buttons = (LinearLayout) convertView.findViewById(R.id.linearLayoutButtons);
        ImageButton upvote = (ImageButton) convertView.findViewById(R.id.imageButtonCommentUpvote);
        ImageButton downvote = (ImageButton) convertView.findViewById(R.id.imageButtonCommentDownvote);
        ImageButton reply = (ImageButton) convertView.findViewById(R.id.imageButtonCommentReply);
        TextView body = (TextView) convertView.findViewById(R.id.textViewComment);
        TextView info = (TextView) convertView.findViewById(R.id.textViewCommentInfo);

        final Comment currentComment = articles.get(position);
        final CommentArrayAdapter adapter = this;

        // Generate text with HTML formatting
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(Html.fromHtml(Html.fromHtml(currentComment.getBody()).toString()));

        // Strip the trailing newline characters that were generated
        ssb.delete(ssb.length() - 2, ssb.length());

        body.setText(ssb);
        body.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable
        layout.setPadding(16 * currentComment.getReplyLevel(), 0, 0, 0);

        String topText =  currentComment.getAuthor() + " &nbsp; <span align='right'><small><b>" +
                currentComment.getScore() + "</b> ( <font color='#66aa66'>" +
                currentComment.getUps() + "</font> | <font color='#aa6666'>" +
                currentComment.getDowns() + "</font> )</small></span>";

        info.setText(Html.fromHtml(topText));

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

        if(currentComment.isUpvoted()){
            upvote.setImageResource(R.drawable.upvoteactive);
        } else{
            upvote.setImageResource(R.drawable.upvote);
        }

        if(currentComment.isDownvoted()){
            downvote.setImageResource(R.drawable.downvoteactive);
        } else{
            downvote.setImageResource(R.drawable.downvote);
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

        // Hide the comment buttons if the user is not logged in
        if(VolleyRequest.cookie == null || VolleyRequest.cookie.equals("")){
            buttons.setVisibility(View.GONE);
        } else {
            //buttons.setVisibility(View.VISIBLE);
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
                adapter.notifyDataSetChanged();
            }
        });

        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentComment.downVote();
                adapter.notifyDataSetChanged();
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext, Reply.class);
                intent.putExtra(PARENT_FULLNAME, currentComment.getFullName());
                thisContext.startActivity(intent);
            }
        });

        return convertView;
    }
}