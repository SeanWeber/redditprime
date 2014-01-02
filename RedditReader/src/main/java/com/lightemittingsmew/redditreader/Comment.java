package com.lightemittingsmew.redditreader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smw on 12/30/13.
 */
public class Comment {
    private String body;
    private ArrayList<Comment> replies;

    Comment(JSONObject jsonComment){
        try {
            this.body = jsonComment.getJSONObject("data").getString("body");
            this.replies = parseCommentArray(jsonComment.getJSONObject("data").getJSONObject("replies").getJSONObject("data").getJSONArray("children"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getBody(){
        return this.body;
    }

    public void setBody(String newBody){
        this.body = newBody;
    }

    public ArrayList<Comment> getReplies(){
        return this.replies;
    }

    public void addReply(Comment reply){
        this.replies.add(reply);
    }

    public static ArrayList<Comment> parseCommentArray(JSONArray commentArray){
        ArrayList<Comment> listComments = new ArrayList<Comment>();
        for(int i=0;i<commentArray.length();i++){
            try {
                Comment tempComment = new Comment(commentArray.getJSONObject(i));
                listComments.add(tempComment);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listComments;
    }
}
