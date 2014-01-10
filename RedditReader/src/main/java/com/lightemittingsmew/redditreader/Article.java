package com.lightemittingsmew.redditreader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    private boolean isSelf;

    private int ups;
    private int downs;
    private int score;
    private int comments;
    private long created;

    public Article(JSONObject jsonArticle){
        try {
            subreddit = jsonArticle.getString("subreddit");
            url = jsonArticle.getString("url");
            permalink = jsonArticle.getString("permalink");
            title = jsonArticle.getString("title");
            author = jsonArticle.getString("author");
            thumbnail = jsonArticle.getString("thumbnail");
            isSelf = jsonArticle.getBoolean("is_self");
            score = jsonArticle.getInt("score");
            ups = jsonArticle.getInt("ups");
            downs = jsonArticle.getInt("downs");
            comments = jsonArticle.getInt("num_comments");
            created = jsonArticle.getLong("created_utc");

            if(isSelf){
                selftext = jsonArticle.getString("selftext");
            }
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

    public boolean isSelf(){
        return isSelf;
    }

    public int getScore(){
        return score;
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
