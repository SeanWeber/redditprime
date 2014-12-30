package com.lightemittingsmew.redditreader;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User extends BaseActivity {
    ListView listViewComments;
    ArrayList<Comment> listComments;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        listViewComments = (ListView)findViewById(R.id.listViewUserPosts);

        if (savedInstanceState == null) {
            userName = VolleyRequest.user;
            final String url = "https://www.reddit.com/user/" + userName + "/.json";
            StringRequest jsonArrayRequest = new RedditRequest(Request.Method.GET, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    parseComments(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            VolleyRequest.queue.add(jsonArrayRequest);

        } else {
            VolleyRequest.initQueue(this.getApplication());
            userName = VolleyRequest.user;
            listComments = (ArrayList<Comment>)savedInstanceState.getSerializable("listUser");
            writeComments();
        }

        setTitle(userName);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable("listUser", listComments);
    }

    private void parseComments(String response){
        JSONArray comments = new JSONArray();
        String op = "";

        try {
            comments = new JSONObject(response).getJSONObject("data").getJSONArray("children");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listComments = Comment.parseCommentArray(comments, op, 0);

        // Parse the comment HTML and format it
        for(Comment comment : listComments)
        {
            comment.parseHeaderText();
            comment.parseBodyText(this);
        }

        writeComments();
    }

    public void writeComments(){
        final UserArrayAdapter commentAdapter = new UserArrayAdapter(this, R.layout.list_user_posts, listComments);
        listViewComments.setAdapter(commentAdapter);
    }
}
