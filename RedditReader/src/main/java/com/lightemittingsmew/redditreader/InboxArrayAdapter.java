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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by smw on 8/26/14.
 */
public class InboxArrayAdapter extends ArrayAdapter<Comment> {
    private Context thisContext;
    private ArrayList<Comment> comments;
    LayoutInflater inflater;

    LinearLayout layout;
    LinearLayout buttons;
    TextView body;
    TextView info;
    TextView linkTitle;
    Button buttonReply;
    Button buttonContext;
    Button buttonComments;

    public InboxArrayAdapter(Context context, int textViewResourceId, ArrayList<Comment> objects){
        super(context, textViewResourceId, objects);
        thisContext = context;
        comments = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the proper views and layouts
        final Comment currentComment = comments.get(position);
        final InboxArrayAdapter adapter = this;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_message, parent, false);
        }
        layout = (LinearLayout) convertView.findViewById(R.id.linearLayoutMessages);
        buttons = (LinearLayout) convertView.findViewById(R.id.linearLayoutMessageButtons);
        body = (TextView) convertView.findViewById(R.id.textViewMessage);
        info = (TextView) convertView.findViewById(R.id.textViewMessageInfo);
        linkTitle = (TextView) convertView.findViewById(R.id.textViewMessageSource);
        buttonReply = (Button) convertView.findViewById(R.id.buttonMessageReply);
        buttonContext = (Button) convertView.findViewById(R.id.buttonMessageContext);
        buttonComments = (Button) convertView.findViewById(R.id.buttonMessageComments);

        linkTitle.setText(currentComment.getLinkTitle());

        layout.setPadding(16 * currentComment.getReplyLevel(), 0, 0, 0);

        setBody(currentComment);
        setTopText(currentComment);
        toggleCommentVisibility(currentComment, position);
        setOnClickListeners(currentComment, adapter);

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

        // Strip the trailing newline characters that were generated
        ssb.delete(ssb.length() - 2, ssb.length());

        body.setText(ssb);
        body.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable
    }

    private void setTopText(Comment comment){
        String userNameString;
        String timeAgoString;

        // Display the sender's username
        userNameString = String.format("%s &nbsp; ", comment.getAuthor());

        // Display how long ago the comment was posted
        timeAgoString = String.format("<span align='right'><small>%s</small></span>", comment.timeAgo());

        String htmlText = userNameString + timeAgoString;
        info.setText(Html.fromHtml(htmlText));
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

    private void setOnClickListeners(final Comment comment, final InboxArrayAdapter adapter){
        final String subreddit = comment.getSubreddit();
        final String parentId = comment.getlinkIdId();
        final String id = comment.getId();

        View.OnClickListener toggleVisibility = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                comment.toggle();
                adapter.notifyDataSetChanged();
            }
        };

        info.setOnClickListener(toggleVisibility);
        body.setOnClickListener(toggleVisibility);

        buttonContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = comment.getContext() + "?context=3";
                Intent intent = new Intent(thisContext, Comments.class);
                intent.putExtra(ArticleArrayAdapter.COMMENT_URL, url);
                thisContext.startActivity(intent);
            }
        });

        buttonReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext, Reply.class);
                intent.putExtra(CommentArrayAdapter.PARENT_FULLNAME, comment.getFullName());
                thisContext.startActivity(intent);
            }
        });

        buttonComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String context = comment.getContext();

                // remove the part of the URL linking to the specific comment
                // by trimming everything after the last slash
                int position = context.lastIndexOf("/");
                String[] a =  {context.substring(0, position), context.substring(position)};
                final String url = a[0] + ".json";

                Intent intent = new Intent(thisContext, Comments.class);
                intent.putExtra(ArticleArrayAdapter.COMMENT_URL, url);
                thisContext.startActivity(intent);
            }
        });
    }
}
