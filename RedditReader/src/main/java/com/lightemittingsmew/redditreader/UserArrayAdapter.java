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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by smw on 5/22/14.
 */
public class UserArrayAdapter extends ArrayAdapter<Comment> {
    public static final String PARENT_FULLNAME = "com.lightemittingsmew.redditreader.PARENT_FULLNAME";
    private Context thisContext;
    private ArrayList<Comment> comments;
    LayoutInflater inflater;

    LinearLayout layout;
    LinearLayout buttons;
    TextView body;
    TextView info;
    TextView linkTitle;
    Button buttonPermalink;
    Button buttonContext;
    Button buttonComments;

    public UserArrayAdapter(Context context, int textViewResourceId, ArrayList<Comment> objects){
        super(context, textViewResourceId, objects);
        thisContext = context;
        comments = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the proper views and layouts
        final Comment currentComment = comments.get(position);
        final UserArrayAdapter adapter = this;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_user_posts, parent, false);
        }
        layout = (LinearLayout) convertView.findViewById(R.id.linearLayoutComment);
        buttons = (LinearLayout) convertView.findViewById(R.id.linearLayoutButtons);
        body = (TextView) convertView.findViewById(R.id.textViewComment);
        info = (TextView) convertView.findViewById(R.id.textViewCommentInfo);
        linkTitle = (TextView) convertView.findViewById(R.id.textViewArticle);
        buttonPermalink = (Button) convertView.findViewById(R.id.buttonPermalink);
        buttonContext = (Button) convertView.findViewById(R.id.buttonContext);
        buttonComments = (Button) convertView.findViewById(R.id.buttonComments);

        linkTitle.setText(currentComment.getLinkTitle());

        layout.setPadding(16 * currentComment.getReplyLevel(), 0, 0, 0);

        body.setText(currentComment.getParsedBody());
        body.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable

        setTopText(currentComment);
        toggleCommentVisibility(currentComment, position);
        setOnClickListeners(currentComment, adapter);

        // Hide the comment buttons if the user is not logged in
        if(VolleyRequest.cookie == null || VolleyRequest.cookie.equals("")){
            buttons.setVisibility(View.GONE);
        }

        return convertView;
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

    private void setOnClickListeners(final Comment comment, final UserArrayAdapter adapter){
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

        buttonPermalink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String url = "https://www.reddit.com/r/"+ subreddit + "/comments/"
                        + parentId +".json?comment=" + id;
                Intent intent = new Intent(thisContext, Comments.class);
                intent.putExtra(ArticleArrayAdapter.COMMENT_URL, url);
                thisContext.startActivity(intent);
            }
        });

        buttonContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = "https://www.reddit.com/r/"+ subreddit + "/comments/"
                        + parentId +".json?comment=" + id + "&context=3";
                Intent intent = new Intent(thisContext, Comments.class);
                intent.putExtra(ArticleArrayAdapter.COMMENT_URL, url);
                thisContext.startActivity(intent);
            }
        });

        buttonComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = "https://www.reddit.com/r/"+ subreddit + "/comments/"
                        + parentId +".json";
                Intent intent = new Intent(thisContext, Comments.class);
                intent.putExtra(ArticleArrayAdapter.COMMENT_URL, url);
                thisContext.startActivity(intent);
            }
        });
    }
}