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
    private int ups;
    private int downs;
    private String kind;
    private int replyLevel;
    private boolean isHidden;
    private boolean isCollapsed;
    private boolean isUpvoted;
    private boolean isDownvoted;

    Comment(JSONObject jsonComment){
        try {
            kind = jsonComment.getString("kind");
            String likes = jsonComment.getJSONObject("data").getString("likes");
            body = jsonComment.getJSONObject("data").getString("body_html");
            author = jsonComment.getJSONObject("data").getString("author");
            id = jsonComment.getJSONObject("data").getString("id");
            ups = jsonComment.getJSONObject("data").getInt("ups");
            downs = jsonComment.getJSONObject("data").getInt("downs");

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

    public String getUps(){
        return String.valueOf(ups);
    }

    public String getDowns(){
        return String.valueOf(downs);
    }

    public String getScore(){
        return String.valueOf(ups - downs);
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

    public int getReplyLevel(){
        return replyLevel;
    }

    public void setReplyLevel(int level){
        replyLevel = level;
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
            ups--;
        } else{
            voteDirection = "1";
            ups++;
        }
        isUpvoted = !isUpvoted;
        if(isDownvoted()){
            isDownvoted = false;
            downs--;
        }

        VolleyRequest.vote(voteDirection, fullname);
    }

    public void downVote(){
        final String voteDirection;
        String fullname = "t1_" + id;

        if(isDownvoted){
            voteDirection = "0";
            downs--;
        } else{
            voteDirection = "-1";
            downs++;
        }
        isDownvoted = !isDownvoted;
        if(isUpvoted){
            isUpvoted = false;
            ups--;
        }

        VolleyRequest.vote(voteDirection, fullname);
    }

    public static ArrayList<Comment> parseCommentArray(JSONArray commentArray, int level){
        ArrayList<Comment> listComments = new ArrayList<Comment>();
        for(int i=0;i<commentArray.length();i++){
            try {
                Comment tempComment = new Comment(commentArray.getJSONObject(i));
                if(tempComment.getKind().equals("t1")){
                    tempComment.setReplyLevel(level);
                    listComments.add(tempComment);

                    // Add any replies to the list and set their reply level
                    ArrayList<Comment> replyList = parseCommentArray(commentArray.getJSONObject(i)
                            .getJSONObject("data").getJSONObject("replies").getJSONObject("data")
                            .getJSONArray("children"), level + 1);
                    listComments.addAll(replyList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listComments;
    }
}
