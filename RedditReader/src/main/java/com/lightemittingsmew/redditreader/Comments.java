package com.lightemittingsmew.redditreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Comments extends ActionBarActivity {

    ListView listViewComments;

    private void parseComments(JSONArray response){
        ArrayList<Comment> listComments = new ArrayList<Comment>();

        try {
            JSONArray comments = response.getJSONObject(1).getJSONObject("data").getJSONArray("children");
            listComments = Comment.parseCommentArray(comments);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CommentArrayAdapter commentAdapter = new CommentArrayAdapter(this, R.layout.list_comment, listComments);
        listViewComments.setAdapter(commentAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        listViewComments = (ListView)findViewById(R.id.listViewComments);

        Intent intent = getIntent();
        final String url = "http://www.reddit.com" + intent.getStringExtra(ArticleArrayAdapter.COMMENT_URL) + ".json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                parseComments(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        FrontPage.queue.add(jsonArrayRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comments, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_comments, container, false);
            return rootView;
        }
    }

}
