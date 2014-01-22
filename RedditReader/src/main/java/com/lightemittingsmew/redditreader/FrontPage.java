package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class FrontPage extends ActionBarActivity {

    private Context context;
    ExpandableListView listViewStories;
    ArrayList<Article> listStories;
    ArticleArrayAdapter articleAdapter;

    private void addStories(JSONObject stories){
        ArrayList<Article> newStories = Article.parseArticleList(stories);

        listStories.addAll(newStories);

        if(articleAdapter == null){
            articleAdapter = new ArticleArrayAdapter(this, listStories, FrontPage.this);
            listViewStories.setAdapter(articleAdapter);
        } else {
            ((BaseAdapter)listViewStories.getAdapter()).notifyDataSetChanged();
        }
    }

    public void loadMore(){
        String url = "http://www.reddit.com/.json";

        if(listStories ==  null){
            listStories = new ArrayList<Article>();
        } else {
            Article last = listStories.get(listStories.size() - 1);
            url = url + "?count=" + listStories.size() + "&after=t3_" + last.getId();
        }

        final JsonObjectRequest articleRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                addStories(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                headers.put("Cookie", VolleyRequest.cookie);
                headers.put("User-Agent", "redditReader01");

                return headers;
            }
        };

        VolleyRequest.queue.add(articleRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        context = this;
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        VolleyRequest.initQueue(this.getApplication());
        listViewStories = (ExpandableListView) findViewById(R.id.listViewStories);

        loadMore();

        // Only load HD thumbnails when connected to wi-fi
        VolleyRequest.loadHdThumbnails = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.front_page, menu);
        MenuItem logout = menu.findItem(R.id.action_logout);
        MenuItem login = menu.findItem(R.id.action_login);

        if(VolleyRequest.cookie == null || VolleyRequest.cookie.equals("")){
            logout.setVisible(false);
            login.setVisible(true);
        } else {
            logout.setVisible(true);
            login.setVisible(false);
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
                Intent intent = new Intent(context, Login.class);
                context.startActivity(intent);
            }
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
            View rootView = inflater.inflate(R.layout.fragment_front_page, container, false);
            return rootView;
        }
    }

}
