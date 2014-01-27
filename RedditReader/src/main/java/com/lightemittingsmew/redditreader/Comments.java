package com.lightemittingsmew.redditreader;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Comments extends ActionBarActivity {

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
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
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

    private void parseComments(JSONArray response){
        final ArrayList<Comment> listComments;
        JSONArray comments = new JSONArray();

        try {
            comments = response.getJSONObject(1).getJSONObject("data").getJSONArray("children");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listComments = Comment.parseCommentArray(comments, 0);

        listViewComments.addHeaderView(headerView());
        final CommentArrayAdapter commentAdapter = new CommentArrayAdapter(this, R.layout.list_comment, listComments);
        listViewComments.setAdapter(commentAdapter);
    }

    private View headerView(){
        View header = getLayoutInflater().inflate(R.layout.list_article_summary, null);
        Article article = Article.getCurrentArticle();
        TextView textViewTitle = (TextView) header.findViewById(R.id.textViewTitle);
        TextView textViewScore = (TextView) header.findViewById(R.id.textViewScore);
        NetworkImageView thumbnail = (NetworkImageView) header.findViewById(R.id.imageViewThumbnail);

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

        TextView txtListChild = (TextView) header.findViewById(R.id.textViewChild);

        String articleUrl = article.getUrl();

        if(article.isSelf()){
            txtListChild.setVisibility(View.VISIBLE);
            txtListChild.setText(Html.fromHtml(Html.fromHtml(article.getSelftext()).toString()));
            txtListChild.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable
        } else {
            txtListChild.setText("");
            txtListChild.setVisibility(View.GONE);
        }

        NetworkImageView img = (NetworkImageView) header.findViewById(R.id.imageViewfull);
        img.setImageUrl(null, VolleyRequest.imageLoader);

        if(article.isImage()){
            img.setImageUrl(articleUrl, VolleyRequest.imageLoader);
            img.setAdjustViewBounds(true);
        } else {
            img.setAdjustViewBounds(false);
        }

        return header;
    }
}
