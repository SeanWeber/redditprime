package com.lightemittingsmew.redditreader;

import android.text.Html;
import android.text.SpannableStringBuilder;

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
    private String kind;
    private int replyLevel;
    private boolean isHidden;
    private boolean isCollapsed;
    private boolean isUpvoted;
    private boolean isDownvoted;
    private boolean isOp;

    Comment(JSONObject jsonComment){
        try {
            kind = jsonComment.getString("kind");
            String likes = jsonComment.getJSONObject("data").getString("likes");
            body = jsonComment.getJSONObject("data").getString("body_html");
            author = jsonComment.getJSONObject("data").getString("author");
            id = jsonComment.getJSONObject("data").getString("id");
            score = jsonComment.getJSONObject("data").getInt("score");

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

            if(jsonComment.getJSONObject("data").has("link_title")){
                linkTitle = jsonComment.getJSONObject("data").getString("link_title");
            }
            if(jsonComment.getJSONObject("data").has("subreddit")){
                subreddit = jsonComment.getJSONObject("data").getString("subreddit");
            }
            if(jsonComment.getJSONObject("data").has("parent_id")){
                String fullLinkId = jsonComment.getJSONObject("data").getString("link_id");
                linkId = fullLinkId.substring(3, fullLinkId.length());
            }
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
        return "t1_" + id;
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
            score--;
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listComments;
    }
}
