package com.lightemittingsmew.redditreader;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.List;

public class SubredditSearch extends ActionBarActivity {

    private Button searchButton;
    private EditText searchQuery;
    private ListView subredditResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit_search);

        searchQuery  = (EditText) findViewById(R.id.textSubredditSearch);
        searchButton = (Button) findViewById(R.id.buttonSubredditSearch);
        subredditResults = (ListView) findViewById(R.id.listViewsubredditResults);

        // Close the keyboard when the search button is pressed
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            search(searchQuery.getText().toString());
            hideSoftKeyboard(SubredditSearch.this);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.subreddit_search, menu);
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

    private void search(String query){
        String url = "http://www.reddit.com/subreddits/search.json?q=" + query;

        final StringRequest subredditRequest = new RedditRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                populateResults(response);
            }
        });

        VolleyRequest.queue.add(subredditRequest);
    }

    private void populateResults(String response){
        List<SubredditResult> subreddits = SubredditResult.parseSubredditList(response);

        // Add the list of subreddits to the list view
        final SubredditArrayAdapter commentAdapter = new SubredditArrayAdapter(this, R.layout.list_comment, subreddits);
        subredditResults.setAdapter(commentAdapter);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
