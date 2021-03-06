package com.lightemittingsmew.redditreader;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.Time;
import android.text.style.URLSpan;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smw on 12/30/13.
 */
public class Comment implements java.io.Serializable{
    private String body;
    private String author;
    private String id;
    private String linkTitle;
    private String subreddit;
    private String linkId;
    private int score;
    private int gilded;
    private long createdUTC;
    private String kind;
    private int replyLevel;
    private boolean isHidden;
    private boolean isCollapsed;
    private boolean isUpvoted;
    private boolean isDownvoted;
    private boolean isOp;
    private String edited;
    private String contextLink;

    transient private Spanned parsedBody;
    transient private Spanned parsedHeader;

    Comment(JSONObject jsonComment){
        try {
            String likes = "";

            kind = jsonComment.getString("kind");
            body = jsonComment.getJSONObject("data").getString("body_html");
            author = jsonComment.getJSONObject("data").getString("author");
            id = jsonComment.getJSONObject("data").getString("id");
            createdUTC = jsonComment.getJSONObject("data").getLong("created_utc");

            if(jsonComment.getJSONObject("data").has("score")){
                likes = jsonComment.getJSONObject("data").getString("likes");
            }
            if(jsonComment.getJSONObject("data").has("score")){
                score = jsonComment.getJSONObject("data").getInt("score");
            }
            if(jsonComment.getJSONObject("data").has("edited")){
                edited = jsonComment.getJSONObject("data").getString("edited");
            }
            if(jsonComment.getJSONObject("data").has("gilded")){
                gilded = jsonComment.getJSONObject("data").getInt("gilded");
            }
            if(jsonComment.getJSONObject("data").has("link_title")){
                linkTitle = jsonComment.getJSONObject("data").getString("link_title");
            }
            if(jsonComment.getJSONObject("data").has("subreddit")){
                subreddit = jsonComment.getJSONObject("data").getString("subreddit");
            }
            if(jsonComment.getJSONObject("data").has("link_id")){
                String fullLinkId = jsonComment.getJSONObject("data").getString("link_id");
                linkId = fullLinkId.substring(3, fullLinkId.length());
            }
            if(jsonComment.getJSONObject("data").has("context")){
                contextLink = jsonComment.getJSONObject("data").getString("context");
            }

            if(likes.equals("true")){
                isUpvoted = true;
                isDownvoted = false;
            } else if(likes.equals("false")){
                isDownvoted = true;
                isUpvoted = false;
            }else{
                isUpvoted = false;
                isDownvoted = false;
            }
            isHidden = false;
            isCollapsed = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getBody(){
        return body;
    }

    public String getScore(){
        return String.valueOf(score);
    }

    public String getAuthor(){
        return author;
    }

    public String getKind(){
        return kind;
    }

    public String getFullName(){
        return kind + "_" + id;
    }

    public String getLinkTitle(){
        if(linkTitle == null){
            return "";
        }
        return linkTitle;
    }

    public String getId(){
        return id;
    }

    public String getSubreddit(){
        return subreddit;
    }

    public String getlinkIdId(){
        return linkId;
    }

    public int getReplyLevel(){
        return replyLevel;
    }

    public void setReplyLevel(int level){
        replyLevel = level;
    }

    public void setOp(){
        isOp = true;
    }

    public boolean isOp(){
        return isOp;
    }

    public boolean isHidden(){
        return isHidden;
    }

    public boolean isCollapsed(){
        return isCollapsed;
    }

    public boolean isUpvoted(){
        return isUpvoted;
    }

    public boolean isDownvoted() {
        return isDownvoted;
    }

    public void hide(){
        isHidden = true;
    }

    public void unhide(){
        isHidden = false;
    }

    public void toggle(){
        if(this.isCollapsed) {
            isCollapsed = false;
        } else {
            isCollapsed = true;
        }
    }

    public void upVote(){
        final String voteDirection;
        String fullname = "t1_" + id;

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
            score++;
        }

        VolleyRequest.vote(voteDirection, fullname);
    }

    public void downVote(){
        final String voteDirection;
        String fullname = "t1_" + id;

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

    public boolean isEdited(){
        // This json element comes in as either "false" or the unix timestamp
        // of the last time the comment was edited
        Boolean result;

        if(edited == null){
            result = false;
        } else {
            result = !edited.equals("false");
        }

        return result;
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
        long timeDifference = currentTime - (createdUTC * 1000);

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

    /*
    Generate a hexadecimal color string based on a comment's score
    Color becomes more green with upvotes and red with downvotes
     */
    private String pointsColor(){
        String color = "777777";

        if(!VolleyRequest.disableScoreColor){
            int red   = 120;
            int green = 120;
            int blue  = 120;

            if(score < 0){
                // Negative scores will gain saturation quicker as negative scoring
                // comments are less likely to be seen
                red -= score * 8;
                red = Math.min(red, 190);
            } else if(score > 0){
                green += score * 2;
                green = Math.min(green, 150);
            }
            color = Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
        }

        return color;
    }

    public String getTopText(){
        String userNameString;
        String pointsString;
        String timeAgoString;
        String editedString = "";
        String gildedString = "";

        // Highlight the original poster's name
        if(isOp()){
            userNameString = String.format("<b><font color='#6666ee'>%s</font></b> &nbsp; ", getAuthor());
        } else {
            userNameString = String.format("%s &nbsp; ", getAuthor());
        }

        // Display the number of points and color the text
        pointsString = String.format("<span align='right'><small><b><font color='#%s'>%s points</font></b>&nbsp; ",
                pointsColor(), getScore());

        // Display how long ago the comment was posted
        timeAgoString = String.format("%s</small></span>", timeAgo());

        // Indicate whether the comment was edited
        if(isEdited()){
            editedString = "*";
        }

        // Indicate whether a comment received reddit gold
        if(gilded > 0){
            gildedString = String.format(" <font color='#aaaa00'>x%d</font>", gilded);
        }

        return userNameString + pointsString + timeAgoString + editedString + gildedString;
    }

    public String getContext(){
        String[] parts = contextLink.split("\\?");
        return "https://www.reddit.com" + parts[0] + ".json";
    }

    public void parseBodyText(Context context)
    {
        // Generate text with HTML formatting
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        // First pass through the HTML parser replaces all character entities,
        // such as &gt; with their actual character, such as >
        String htmlString = Html.fromHtml(body).toString();

        // Remove any paragraph tags nested within list elements. This causes
        // superfluous newline characters when parsed
        htmlString = htmlString.replaceAll("<li><p>", "<li>");
        htmlString = htmlString.replaceAll("</li></p>", "</li>");

        // Parse the HTML
        Spanned parsedHtml = Html.fromHtml(htmlString, null, new RedditTagHandler());
        ssb.append(parsedHtml);

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

        parsedBody = ssb;
    }

    public void parseHeaderText(){
        parsedHeader = Html.fromHtml(getTopText());
    }

    public Spanned getParsedBody(){
        return parsedBody;
    }

    public Spanned getParsedHeader(){
        return parsedHeader;
    }

    public static ArrayList<Comment> parseCommentArray(JSONArray commentArray, String op, int level){
        ArrayList<Comment> listComments = new ArrayList<Comment>();
        for(int i=0;i<commentArray.length();i++){
            try {
                Comment tempComment = new Comment(commentArray.getJSONObject(i));
                if(tempComment.getKind().equals("t1")){
                    // Mark the original poster's comments
                    if(op.equals(tempComment.getAuthor())){
                        tempComment.setOp();
                    }

                    tempComment.setReplyLevel(level);
                    listComments.add(tempComment);

                    // Add any replies to the list and set their reply level
                    ArrayList<Comment> replyList = parseCommentArray(commentArray.getJSONObject(i)
                            .getJSONObject("data").getJSONObject("replies").getJSONObject("data")
                            .getJSONArray("children"), op, level + 1);
                    listComments.addAll(replyList);
                } else if(tempComment.getKind().equals("t4")){
                    // Sent message
                    listComments.add(tempComment);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listComments;
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

