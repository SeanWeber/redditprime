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

public class User extends ActionBarActivity {
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
            final String url = "http://www.reddit.com/user/" + userName + "/.json";
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        MenuItem logout = menu.findItem(R.id.action_logout);
        MenuItem login = menu.findItem(R.id.action_login);
        MenuItem user = menu.findItem(R.id.action_user);

        if(VolleyRequest.cookie == null || VolleyRequest.cookie.equals("")){
            user.setVisible(false);
            logout.setVisible(false);
            login.setVisible(true);
        } else {
            user.setVisible(true);
            logout.setVisible(true);
            login.setVisible(false);

            // Set the title the the username of the logged in user
            user.setTitle(VolleyRequest.user);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_logout:{
                VolleyRequest.logout(this);
                finish();
                startActivity(getIntent());
                break;
            }
            case R.id.action_login:{
                Intent intent = new Intent(this, Login.class);
                this.startActivity(intent);
                break;
            }
            case R.id.action_user:{
                Intent intent = new Intent(this, User.class);
                this.startActivity(intent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
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
        writeComments();
    }

    public void writeComments(){
        final UserArrayAdapter commentAdapter = new UserArrayAdapter(this, R.layout.list_user_posts, listComments);
        listViewComments.setAdapter(commentAdapter);
    }
}
