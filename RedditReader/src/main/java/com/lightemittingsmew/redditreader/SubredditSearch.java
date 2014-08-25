package com.lightemittingsmew.redditreader;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(searchQuery.getText().toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.subreddit_search, menu);
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
}
