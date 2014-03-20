package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class Comments extends ActionBarActivity {
    public static final String ARTICLE_URL = "com.lightemittingsmew.redditreader.ARTICLE_URL";
    public static final String PARENT_FULLNAME = "com.lightemittingsmew.redditreader.PARENT_FULLNAME";
    ListView listViewComments;
    String curl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        listViewComments = (ListView)findViewById(R.id.listViewComments);

        Intent intent = getIntent();
        String commentURL = intent.getStringExtra(ArticleArrayAdapter.COMMENT_URL);
        curl = "http://www.reddit.com" + commentURL + ".json";

        final String url = curl;
        StringRequest jsonArrayRequest = new RedditRequest(Request.Method.GET, url, new Response.Listener<String>() {

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarComment);
        final ArrayList<Comment> listComments;
        JSONArray comments = new JSONArray();

        try {
            comments = new JSONArray(response).getJSONObject(1).getJSONObject("data").getJSONArray("children");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listComments = Comment.parseCommentArray(comments, 0);

        listViewComments.addHeaderView(headerView());
        final CommentArrayAdapter commentAdapter = new CommentArrayAdapter(this, R.layout.list_comment, listComments);
        listViewComments.setAdapter(commentAdapter);

        progressBar.setVisibility(View.GONE);
    }

    private View headerView(){
        final Article article = Article.getCurrentArticle();
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
            txtListChild.setVisibility(View.VISIBLE);
            txtListChild.setText(Html.fromHtml(Html.fromHtml(article.getSelftext()).toString()));
            txtListChild.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable
        } else {
            txtListChild.setText("");
            txtListChild.setVisibility(View.GONE);
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
                context.startActivity(intent);
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
