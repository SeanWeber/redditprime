package com.lightemittingsmew.redditreader;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Inbox extends BaseActivity {
    public static final String NEW_MESSAGE = "com.lightemittingsmew.redditreader.NEW_MESSAGE";

    final String urlAll = "https://www.reddit.com/message/inbox/.json";
    final String urlUnread = "https://www.reddit.com/message/unread.json";
    final String urlSent = "https://www.reddit.com/message/sent.json";

    Button buttonUnreadMessages;
    Button buttonSentMessages;
    Button buttonAllMessages;

    ProgressBar progressBar;
    ListView listViewComments;
    TextView emptyMessage;
    ArrayList<Comment> listComments;
    String userName;
    Boolean markCommentsRead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        listViewComments = (ListView)findViewById(R.id.listViewMessages);
        progressBar = (ProgressBar)findViewById(R.id.progressBarInbox);
        emptyMessage = (TextView)findViewById(R.id.textViewEmptyMessage);

        buttonUnreadMessages = (Button)findViewById(R.id.buttonUnreadMessages);
        buttonSentMessages = (Button)findViewById(R.id.buttonSentMessages);
        buttonAllMessages = (Button)findViewById(R.id.buttonAllMessages);

        if (savedInstanceState == null) {
            userName = VolleyRequest.user;

            Intent intent = getIntent();
            final String newMessage = intent.getStringExtra(NEW_MESSAGE);

            if(newMessage == null){
                onClickAll(buttonAllMessages);
            } else {
                onClickUnread(buttonUnreadMessages);
            }

        } else {
            VolleyRequest.initQueue(this.getApplication());
            userName = VolleyRequest.user;
            listComments = (ArrayList<Comment>)savedInstanceState.getSerializable("listUser");
            writeComments();
        }
    }

    private void loadInbox(String url){
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
        final InboxArrayAdapter commentAdapter = new InboxArrayAdapter(this, R.layout.list_user_posts, listComments);
        listViewComments.setAdapter(commentAdapter);

        if(listComments.size() == 0){
            emptyMessage.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(View.GONE);
        listViewComments.setVisibility(View.VISIBLE);

        if(markCommentsRead){
            markMessagesRead();
        }
    }

    public void onClickUnread(View v){
        clearButtonColors();
        v.setBackgroundColor(Color.GRAY);

        markCommentsRead = true;

        loadInbox(urlUnread);
    }

    public void onClickAll(View v){
        clearButtonColors();
        v.setBackgroundColor(Color.GRAY);

        loadInbox(urlAll);
    }

    public void onClickSent(View v){
        clearButtonColors();
        v.setBackgroundColor(Color.GRAY);

        loadInbox(urlSent);
    }

    private void clearButtonColors(){
        int inactiveColor = Color.LTGRAY;
        findViewById(R.id.buttonUnreadMessages).setBackgroundColor(inactiveColor);
        findViewById(R.id.buttonSentMessages).setBackgroundColor(inactiveColor);
        findViewById(R.id.buttonAllMessages).setBackgroundColor(inactiveColor);

        progressBar.setVisibility(View.VISIBLE);
        listViewComments.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.GONE);
    }

    private void markMessagesRead(){
        String names = "";
        for(Comment c : listComments){
            names += c.getFullName();
            names += ",";
        }

        final String fullnames = names;
        StringRequest readRequest = new StringRequest(Request.Method.POST, "https://www.reddit.com/api/read_message", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                markCommentsRead = false;
                VolleyRequest.hasNewMessage = false;
                supportInvalidateOptionsMenu();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                headers.put("Cookie", VolleyRequest.cookie);
                headers.put("User-Agent", VolleyRequest.APP_VERSION);

                return headers;
            }
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("id", fullnames);
                params.put("uh", VolleyRequest.modhash);

                return params;
            }
        };

        VolleyRequest.queue.add(readRequest);
    }
}
