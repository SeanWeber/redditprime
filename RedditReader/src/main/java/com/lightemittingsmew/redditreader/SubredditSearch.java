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

public class SubredditSearch extends BaseActivity {

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

    private void search(String query){
        String url = "https://www.reddit.com/subreddits/search.json?q=" + query;

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
