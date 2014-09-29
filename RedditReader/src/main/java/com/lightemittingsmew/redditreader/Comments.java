package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Comments extends BaseActivity {
    public static final String ARTICLE_URL = "com.lightemittingsmew.redditreader.ARTICLE_URL";
    public static final String PARENT_FULLNAME = "com.lightemittingsmew.redditreader.PARENT_FULLNAME";
    public static final int COMMENT_REPLY_REQUEST = 1;
    ListView listViewComments;
    ArrayList<Comment> listComments;
    ProgressBar progressBar;
    Article currentArticle;
    CommentArrayAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        listViewComments = (ListView)findViewById(R.id.listViewComments);
        progressBar = (ProgressBar) findViewById(R.id.progressBarComment);
        Intent intent = getIntent();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

            final String commentURL = intent.getStringExtra(ArticleArrayAdapter.COMMENT_URL);

            StringRequest jsonArrayRequest = new RedditRequest(Request.Method.GET, commentURL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    parseComments(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            VolleyRequest.queue.add(jsonArrayRequest);
        } else {
            VolleyRequest.initQueue(this.getApplication());
            listComments = (ArrayList<Comment>)savedInstanceState.getSerializable("listComments");
            currentArticle = (Article)savedInstanceState.getSerializable("currentArticle");
            writeComments();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable("listComments", listComments);
        outState.putSerializable("currentArticle", currentArticle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == COMMENT_REPLY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String parentFullname = data.getStringExtra("result");
                addNewComment(parentFullname);
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_comments, container, false);
            return rootView;
        }
    }

    private void parseComments(String response){
        JSONArray comments = new JSONArray();
        String op = "";

        try {
            comments = new JSONArray(response).getJSONObject(1).getJSONObject("data").getJSONArray("children");
            op = new JSONArray(response).getJSONObject(0).getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("author");
            currentArticle = new Article(new JSONArray(response).getJSONObject(0).getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listComments = Comment.parseCommentArray(comments, op, 0);
        writeComments();
    }

    public void writeComments(){
        listViewComments.addHeaderView(headerView());
        commentAdapter = new CommentArrayAdapter(this, R.layout.list_comment, listComments);
        listViewComments.setAdapter(commentAdapter);

        progressBar.setVisibility(View.GONE);
    }

    private void addNewComment(String parentFullname){
        final String parentName = parentFullname.substring(3);

        final String commentURL = "http://www.reddit.com/r/"+ currentArticle.getSubreddit() + "/comments/"
                + currentArticle.getId() +".json?comment=" + parentName;

        StringRequest commentReplyRequest = new RedditRequest(Request.Method.GET, commentURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                insertNewComment(response, parentName);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        VolleyRequest.queue.add(commentReplyRequest);
    }

    private void insertNewComment(String response, String parentId){
        Comment newReply = null;

        // Find all replies to the parent comment and check if the current user is the author of
        // any of them
        try {
            JSONArray replies = new JSONArray(response).getJSONObject(1).getJSONObject("data")
                    .getJSONArray("children").getJSONObject(0).getJSONObject("data")
                    .getJSONObject("replies").getJSONObject("data").getJSONArray("children");

            for(int i=0;i<replies.length();i++){
                Comment tempComment = new Comment(replies.getJSONObject(i));
                if(tempComment.getAuthor().equals(VolleyRequest.user)){
                    newReply = tempComment;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(newReply != null){

            // Find the parent comment in the comments list
            for(int i=0;i<listComments.size();i++){
                if(listComments.get(i).getId().equals(parentId)){

                    // Set the new comments reply level so it indents correctly
                    int replyLevel = listComments.get(i).getReplyLevel() + 1;
                    newReply.setReplyLevel(replyLevel);

                    listComments.add(i + 1, newReply);

                    commentAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    private View headerView(){
        final Article article = currentArticle;
        final Context context = this;
        View header = getLayoutInflater().inflate(R.layout.list_article_summary, null);
        final View child = getLayoutInflater().inflate(R.layout.list_article_child, null);
        ((ViewGroup)header).addView(child);
        TextView textViewTitle = (TextView) header.findViewById(R.id.textViewTitle);
        TextView textViewScore = (TextView) header.findViewById(R.id.textViewScore);
        NetworkImageView thumbnail = (NetworkImageView) header.findViewById(R.id.imageViewThumbnail);
        ImageButton buttonComment = (ImageButton) child.findViewById(R.id.buttonComments);
        ImageButton buttonArticle = (ImageButton) child.findViewById(R.id.buttonArticle);
        final ImageButton buttonUpvote = (ImageButton) child.findViewById(R.id.buttonUpvote);
        final ImageButton buttonDownvote = (ImageButton) child.findViewById(R.id.buttonDownvote);
        LinearLayout buttonArea = (LinearLayout) child.findViewById(R.id.linearLayoutButtonArea);

        String thumbnailUrl = article.getThumbnail();
        String score = "<font color='#666666'>" + article.getScore() +
                " points by " + article.getAuthor() + " in /r/" +
                article.getSubreddit() + "</font>";

        textViewTitle.setText(article.getTitle());
        textViewScore.setText(Html.fromHtml(score));

        thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
        thumbnail.setBackgroundColor(Color.parseColor("#336699"));
        thumbnail.setDefaultImageResId(R.drawable.rr5);
        thumbnail.setErrorImageResId(R.drawable.errorthumbnail);
        thumbnail.setImageUrl(null, VolleyRequest.imageLoader);

        if(thumbnailUrl.equals("default") || thumbnailUrl.equals("")){}
        else if(thumbnailUrl.equals("self")){}
        else if(thumbnailUrl.equals("nsfw")){}
        else if(article.isImage() && VolleyRequest.loadHdThumbnails){
            thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbnail.setImageUrl(article.getUrl(), VolleyRequest.imageLoader);
        }
        else{
            try{
                thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
                thumbnail.setImageUrl(thumbnailUrl, VolleyRequest.imageLoader);
            }
            catch(Exception e){
                e.printStackTrace();
                Log.e("URL", thumbnailUrl);
            }
        }

        TextView txtListChild = (TextView) child.findViewById(R.id.textViewChild);

        if(article.isSelf()){
            SpannableStringBuilder ssb;
            txtListChild.setVisibility(View.VISIBLE);

            // Generate text with HTML formatting
            ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml(Html.fromHtml(article.getSelftext()).toString()));

            // Strip the trailing newline characters that were generated
            ssb.delete(ssb.length() - 2, ssb.length());

            txtListChild.setText(ssb);
            txtListChild.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable
        } else {
            txtListChild.setText("");
            txtListChild.setVisibility(View.GONE);
        }

        // Hide the comment buttons if the user is not logged in
        if(VolleyRequest.cookie == null || VolleyRequest.cookie.equals("")){
            buttonArea.setVisibility(View.GONE);
        }

        if(article.isUpvoted()){
            buttonUpvote.setImageResource(R.drawable.upvoteactive);
        } else{
            buttonUpvote.setImageResource(R.drawable.upvote);
        }

        if(article.isDownvoted()){
            buttonDownvote.setImageResource(R.drawable.downvoteactive);
        } else{
            buttonDownvote.setImageResource(R.drawable.downvote);
        }

        buttonUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                article.upVote();

                if(article.isUpvoted()){
                    buttonUpvote.setImageResource(R.drawable.upvoteactive);
                } else{
                    buttonUpvote.setImageResource(R.drawable.upvote);
                }
                if(article.isDownvoted()){
                    buttonDownvote.setImageResource(R.drawable.downvoteactive);
                } else{
                    buttonDownvote.setImageResource(R.drawable.downvote);
                }
            }
        });

        buttonDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                article.downVote();

                if(article.isUpvoted()){
                    buttonUpvote.setImageResource(R.drawable.upvoteactive);
                } else{
                    buttonUpvote.setImageResource(R.drawable.upvote);
                }
                if(article.isDownvoted()){
                    buttonDownvote.setImageResource(R.drawable.downvoteactive);
                } else{
                    buttonDownvote.setImageResource(R.drawable.downvote);
                }
            }
        });

        buttonArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Articles.class);
                intent.putExtra(ARTICLE_URL, article.getUrl());
                context.startActivity(intent);
            }
        });

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Reply.class);
                intent.putExtra(PARENT_FULLNAME, article.getFullName());
                startActivityForResult(intent, COMMENT_REPLY_REQUEST);
            }
        });

        textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(child.getVisibility() == View.VISIBLE){
                    child.setVisibility(View.GONE);
                }else{
                    child.setVisibility(View.VISIBLE);
                }
            }
        });
        return header;
    }
}
