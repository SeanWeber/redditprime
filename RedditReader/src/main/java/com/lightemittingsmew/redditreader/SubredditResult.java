package com.lightemittingsmew.redditreader;

import android.text.Html;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smw on 8/24/14.
 */
public class SubredditResult {
    private String url;
    private String fullDescription;
    private String publicDescription;
    private int subscribers;
    private Boolean over18;

    public SubredditResult(JSONObject jsonArticle){
        try {
            url = jsonArticle.getString("url");
            publicDescription = jsonArticle.getString("public_description_html");
            fullDescription = jsonArticle.getString("description_html");
            subscribers = jsonArticle.getInt("subscribers");
            over18 = jsonArticle.getBoolean("over18");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<SubredditResult> parseSubredditList(String stories){
        ArrayList<SubredditResult> subredditList = new ArrayList<SubredditResult>();
        JSONArray jsonArraySubreddits = new JSONArray();
        try {
            jsonArraySubreddits = new JSONObject(stories).getJSONObject("data").getJSONArray("children");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<jsonArraySubreddits.length();i++){
            JSONObject currentSubreddit = null;
            try {
                currentSubreddit = jsonArraySubreddits.getJSONObject(i).getJSONObject("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            subredditList.add(new SubredditResult(currentSubreddit));
        }

        return subredditList;
    }

    public String getName(){
        return url;
    }

    public Spanned getDescription(){
        String descriptionText;

        if(publicDescription.equals("null")){
            descriptionText = fullDescription;
        } else {
            descriptionText = publicDescription;
        }

        String descriptionHtml = Html.fromHtml(descriptionText).toString();
        return Html.fromHtml(descriptionHtml);
    }
}
