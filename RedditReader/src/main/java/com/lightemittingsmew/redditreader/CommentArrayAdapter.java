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
import java.util.List;

/**
 * Created by smw on 12/23/13.
 */
public class CommentArrayAdapter extends ArrayAdapter<Comment> {
    public static final String PARENT_FULLNAME = "com.lightemittingsmew.redditreader.PARENT_FULLNAME";
    private Context thisContext;
    private List<Comment> comments;
    LayoutInflater inflater;

    LinearLayout layout;
    LinearLayout buttons;
    ImageButton upvote;
    ImageButton downvote;
    ImageButton reply;
    TextView body;
    TextView info;

    public CommentArrayAdapter(Context context, int textViewResourceId, List<Comment> objects){
        super(context, textViewResourceId, objects);
        thisContext = context;
        comments = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the proper views and layouts
        final Comment currentComment = comments.get(position);
        final CommentArrayAdapter adapter = this;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_comment, parent, false);
        }
        layout = (LinearLayout) convertView.findViewById(R.id.linearLayoutComment);
        buttons = (LinearLayout) convertView.findViewById(R.id.linearLayoutButtons);
        upvote = (ImageButton) convertView.findViewById(R.id.imageButtonCommentUpvote);
        downvote = (ImageButton) convertView.findViewById(R.id.imageButtonCommentDownvote);
        reply = (ImageButton) convertView.findViewById(R.id.imageButtonCommentReply);
        body = (TextView) convertView.findViewById(R.id.textViewComment);
        info = (TextView) convertView.findViewById(R.id.textViewCommentInfo);

        layout.setPadding(16 * currentComment.getReplyLevel(), 0, 0, 0);

        setBody(currentComment);
        setTopText(currentComment);
        toggleCommentVisibility(currentComment, position);
        setOnClickListeners(currentComment, adapter);

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

        // Hide the comment buttons if the user is not logged in
        if(VolleyRequest.cookie == null || VolleyRequest.cookie.equals("")){
            buttons.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void setBody(Comment comment){
        // Generate text with HTML formatting
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(Html.fromHtml(Html.fromHtml(comment.getBody()).toString()));

        if(ssb.length() >= 2){
            // Strip the trailing newline characters that were generated
            ssb.delete(ssb.length() - 2, ssb.length());
        }

        body.setText(ssb);
        body.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable
    }

    private void setTopText(Comment comment){
        info.setText(Html.fromHtml(comment.getTopText()));
    }

    private void toggleCommentVisibility(Comment comment, int position){
        if(comment.isCollapsed()) {
            // Hide all child comments
            int nextComment = position + 1;
            while(nextComment < comments.size() &&
                    comments.get(nextComment).getReplyLevel() > comment.getReplyLevel()){
                comments.get(nextComment).hide();
                nextComment++;
            }
        } else if(!comment.isHidden()) {
            // Show all child comments
            int nextComment = position + 1;
            while(nextComment < comments.size() &&
                    comments.get(nextComment).getReplyLevel() > comment.getReplyLevel()){
                comments.get(nextComment).unhide();
                nextComment++;
            }
        }

        if(comment.isHidden()){
            // Hide the entire comment
            info.setVisibility(View.GONE);
            body.setVisibility(View.GONE);
            buttons.setVisibility(View.GONE);
        } else if(comment.isCollapsed()){
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
    }

    private void setOnClickListeners(final Comment comment, final CommentArrayAdapter adapter){
        View.OnClickListener toggleVisibility = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                comment.toggle();
                adapter.notifyDataSetChanged();
            }
        };

        info.setOnClickListener(toggleVisibility);
        body.setOnClickListener(toggleVisibility);

        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.upVote();
                adapter.notifyDataSetChanged();
            }
        });

        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.downVote();
                adapter.notifyDataSetChanged();
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext, Reply.class);
                intent.putExtra(PARENT_FULLNAME, comment.getFullName());
                thisContext.startActivity(intent);
            }
        });
    }
}