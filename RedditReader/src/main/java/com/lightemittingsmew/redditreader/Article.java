package com.lightemittingsmew.redditreader;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by smw on 1/10/14.
 */
public class Article implements java.io.Serializable{
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

    private int score;
    private int comments;
    private long created;

    private static Article currentArticle;

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
            comments = jsonArticle.getInt("num_comments");
            created = jsonArticle.getLong("created_utc");
            isUpvoted = false;
            isDownvoted = false;

            if(isSelf){
                selftext = jsonArticle.getString("selftext_html");
                if (selftext.equals("null")){
                    isSelf = false;
                }
            }

            isImage = ( url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png"));
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

    public String getFullName(){
        return "t3_" + id;
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
            score--;
        } else{
            voteDirection = "1";
            score++;
        }
        isUpvoted = !isUpvoted;
        if(isDownvoted()){
            isDownvoted = false;
            score--;
        }

        VolleyRequest.vote(voteDirection, fullname);
    }

    public void downVote(){
        final String voteDirection;
        String fullname = "t3_" + id;

        if(isDownvoted){
            voteDirection = "0";
            score++;
        } else{
            voteDirection = "-1";
            score--;
        }
        isDownvoted = !isDownvoted;
        if(isUpvoted){
            isUpvoted = false;
            score--;
        }

        VolleyRequest.vote(voteDirection, fullname);
    }

    public boolean isDownvoted(){
        return isDownvoted;
    }

    public String getScore(){
        return String.valueOf(score);
    }

    public int getComments(){
        return comments;
    }

    public String timeAgo(){
        long SECOND = 1000;
        long MINUTE = SECOND * 60;
        long HOUR = MINUTE * 60;
        long DAY = HOUR * 24;
        long WEEK = DAY * 7;
        long MONTH = DAY * 30;
        long YEAR = DAY * 365;

        String resultString;
        String unit;
        long unitsPast;

        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - (created * 1000);

        // Calculate which unit to display
        if(timeDifference > YEAR){
            unit = "year";
            unitsPast = timeDifference / YEAR;
        } else if (timeDifference > MONTH) {
            unit = "month";
            unitsPast = timeDifference / MONTH;
        } else if (timeDifference > WEEK) {
            unit = "week";
            unitsPast = timeDifference / WEEK;
        } else if (timeDifference > DAY) {
            unit = "day";
            unitsPast = timeDifference / DAY;
        } else if (timeDifference > HOUR) {
            unit = "hour";
            unitsPast = timeDifference / HOUR;
        } else if (timeDifference > MINUTE) {
            unit = "minute";
            unitsPast = timeDifference / MINUTE;
        } else {
            unit = "second";
            unitsPast = timeDifference / SECOND;
        }

        // Use plural unit names if there is more than one
        if(unitsPast == 1){
            resultString = "1 " + unit + " ago";
        } else {
            resultString = unitsPast + " " + unit + "s ago";
        }

        return resultString;
    }

    public static ArrayList<Article> parseArticleList(String stories){
        ArrayList<Article> articleList = new ArrayList<Article>();
        JSONArray jsonArrayStories = new JSONArray();
        try {
            jsonArrayStories = new JSONObject(stories).getJSONObject("data").getJSONArray("children");
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

    public static void setCurrentArticle(Article current){
        currentArticle = current;
    }

    public static Article getCurrentArticle(){
        return currentArticle;
    }

    public Spanned getParsedBody(Context context){
        // Generate text with HTML formatting
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        // First pass through the HTML parser replaces all character entities,
        // such as &gt; with their actual character, such as >
        String htmlString = Html.fromHtml(selftext).toString();

        // Remove any paragraph tags nested within list elements. This causes
        // superfluous newline characters when parsed
        htmlString = htmlString.replaceAll("<li><p>", "<li>");
        htmlString = htmlString.replaceAll("</li></p>", "</li>");

        // Parse the HTML
        Spanned parsedBody = Html.fromHtml(htmlString, null, new RedditTagHandler());
        ssb.append(parsedBody);

        // Strip the trailing newline characters that were generated by the
        // HTML parser
        if(ssb.length() >= 2){
            ssb.delete(ssb.length() - 2, ssb.length());
        }

        // Make links open in the Articles activity when clicked instead of the
        // web browser
        URLSpan urlSpans[] = ssb.getSpans(0, ssb.length(), URLSpan.class);
        for(URLSpan urlSpan : urlSpans){
            int start = ssb.getSpanStart(urlSpan);
            int end = ssb.getSpanEnd(urlSpan);

            ssb.removeSpan(urlSpan);
            ssb.setSpan(new ActivityURLSpan(urlSpan.getURL(), context), start, end, 0);
        }

        return ssb;
    }

    private static class ActivityURLSpan extends URLSpan {
        private Context context;
        private String urlString;

        public ActivityURLSpan(String url, Context newContext) {
            super(url);
            urlString = url;
            context = newContext;
        }

        @Override
        public void onClick(View widget) {
            try {
                // Open the link in the Articles activity
                Intent intent = new Intent(context, Articles.class);
                intent.putExtra(Articles.ARTICLE_URL, urlString);
                intent.putExtra(Articles.ARTICLE_TITLE, "Link");
                context.startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                // do something useful here
            }
        }
    }
}
