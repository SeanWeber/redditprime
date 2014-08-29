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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Inbox extends ActionBarActivity {
    public static final String NEW_MESSAGE = "com.lightemittingsmew.redditreader.NEW_MESSAGE";

    final String urlAll = "http://www.reddit.com/message/inbox/.json";
    final String urlUnread = "http://www.reddit.com/message/unread.json";
    final String urlSent = "http://www.reddit.com/message/sent.json";

    Button buttonUnreadMessages;
    Button buttonSentMessages;
    Button buttonAllMessages;

    ProgressBar progressBar;
    ListView listViewComments;
    TextView emptyMessage;
    ArrayList<Comment> listComments;
    String userName;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    }

    public void onClickUnread(View v){
        clearButtonColors();
        v.setBackgroundColor(Color.GRAY);

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
}
