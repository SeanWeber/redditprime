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
public class Comment {
    private SpannableStringBuilder body;
    private String author;
    private String id;
    private String ups;
    private String downs;
    private String kind;
    private int replyLevel;
    private boolean isHidden;
    private boolean isCollapsed;

    Comment(JSONObject jsonComment){
        try {
            kind = jsonComment.getString("kind");
            author = jsonComment.getJSONObject("data").getString("author");
            id = jsonComment.getJSONObject("data").getString("id");
            ups = jsonComment.getJSONObject("data").getString("ups");
            downs = jsonComment.getJSONObject("data").getString("downs");
            isHidden = false;
            isCollapsed = false;

            String b = Html.fromHtml(jsonComment.getJSONObject("data").getString("body_html")).toString();
            body = (SpannableStringBuilder)Html.fromHtml(b);
            body.replace(body.length() - 2, body.length() - 1, " ");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public CharSequence getBody(){
        return body;
    }

    public String getUps(){
        return ups;
    }

    public String getDowns(){
        return downs;
    }

    public String getAuthor(){
        return author;
    }

    public String getKind(){
        return kind;
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
