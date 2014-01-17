package com.lightemittingsmew.redditreader;

import android.text.Html;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smw on 1/10/14.
 */
public class Article {
    private String subreddit;
    private String url;
    private String permalink;
    private String title;
    private String selftext;
    private String author;
    private String thumbnail;
    private String id;

    private boolean isSelf;
    private boolean isUpvoted;
    private boolean isDownvoted;
    private boolean isImage;

    private int ups;
    private int downs;
    private int score;
    private int comments;
    private long created;

    public Article(JSONObject jsonArticle){
        try {
            id = jsonArticle.getString("id");
            subreddit = jsonArticle.getString("subreddit");
            url = jsonArticle.getString("url");
            permalink = jsonArticle.getString("permalink");
            title = Html.fromHtml(jsonArticle.getString("title")).toString();
            author = jsonArticle.getString("author");
            thumbnail = jsonArticle.getString("thumbnail");
            isSelf = jsonArticle.getBoolean("is_self");
            score = jsonArticle.getInt("score");
            ups = jsonArticle.getInt("ups");
            downs = jsonArticle.getInt("downs");
            comments = jsonArticle.getInt("num_comments");
            created = jsonArticle.getLong("created_utc");
            isUpvoted = false;
            isDownvoted = false;

            if(isSelf){
                selftext = jsonArticle.getString("selftext_html");
            }

            isImage = ( url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".gif") ||
                    url.endsWith(".png"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle(){
        return title;
    }

    public String getSubreddit(){
        return subreddit;
    }

    public String getUrl(){
        return url;
    }

    public String getPermalink(){
        return permalink;
    }

    public String getAuthor(){
        return author;
    }

    public String getThumbnail(){
        return thumbnail;
    }

    public String getSelftext(){
        return selftext;
    }

    public String getId(){
        return id;
    }

    public boolean isSelf(){
        return isSelf;
    }

    public boolean isUpvoted(){
        return isUpvoted;
    }

    public boolean isImage(){
        return isImage;
    }

    public void upVote(){
        final String voteDirection;
        String fullname = "t3_" + id;

        if(isUpvoted){
            voteDirection = "0";
        } else{
            voteDirection = "1";
        }
        isUpvoted = !isUpvoted;

        VolleyRequest.vote(voteDirection, fullname);
    }

    public void downVote(){
        final String voteDirection;
        String fullname = "t3_" + id;

        if(isDownvoted){
            voteDirection = "0";
        } else{
            voteDirection = "-1";
        }
        isDownvoted = !isDownvoted;

        VolleyRequest.vote(voteDirection, fullname);
    }

    public boolean isDownvoted(){
        return isDownvoted;
    }

    public String getScore(){
        return String.valueOf(score);
    }

    public static ArrayList<Article> parseArticleList(JSONObject stories){
        ArrayList<Article> articleList = new ArrayList<Article>();
        JSONArray jsonArrayStories = new JSONArray();
        try {
            jsonArrayStories = stories.getJSONObject("data").getJSONArray("children");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<jsonArrayStories.length();i++){
            JSONObject currentStory = null;
            try {
                currentStory = jsonArrayStories.getJSONObject(i).getJSONObject("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            articleList.add(new Article(currentStory));
        }

        return articleList;
    }
}
