package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FrontPage extends ActionBarActivity {

    private Context context;
    ExpandableListView listViewStories;
    ArrayList<Article> listStories;
    ArticleArrayAdapter articleAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    String subreddit;
    ProgressBar progressbar;
    final ArrayList<String> subreddits = new ArrayList<String>();

    private void addStories(String stories){
        ArrayList<Article> newStories = Article.parseArticleList(stories);

        listStories.addAll(newStories);

        if(articleAdapter == null){
            articleAdapter = new ArticleArrayAdapter(this, listStories, FrontPage.this);
            listViewStories.setAdapter(articleAdapter);
        } else {
            ((BaseAdapter)listViewStories.getAdapter()).notifyDataSetChanged();
        }

        progressbar.setVisibility(View.GONE);
    }

    public void loadMore(){
        String url = "http://www.reddit.com" + subreddit + "/.json";

        if(listStories == null){
            listStories = new ArrayList<Article>();
        } else if (listStories.size() > 0) {
            Article last = listStories.get(listStories.size() - 1);
            url = url + "?count=" + listStories.size() + "&after=t3_" + last.getId();
        }

        final StringRequest articleRequest = new RedditRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                addStories(response);
            }
        });

        VolleyRequest.queue.add(articleRequest);
    }

    public void fetchSubreddits(String after){
        String url = "http://www.reddit.com/subreddits/.json";

        if(!VolleyRequest.cookie.equals("")){
            url = "http://www.reddit.com/subreddits/mine/subscriber/.json";
            if(!after.equals("")){
                url = url + "?after=" + after;
            }
        }

        final StringRequest subredditRequest = new RedditRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                addSubreddits(response);
            }
        });

        VolleyRequest.queue.add(subredditRequest);
    }

    public void addSubreddits(String response){
        String after = "null";
        try {
            JSONArray ja = new JSONObject(response).getJSONObject("data").getJSONArray("children");
            for(int i=0;i<ja.length();i++){
                String subredditName = ja.getJSONObject(i).getJSONObject("data").getString("url");
                subredditName = subredditName.substring(0, subredditName.lastIndexOf("/"));
                subreddits.add(subredditName);
            }
            after = new JSONObject(response).getJSONObject("data").getString("after");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(!after.equals("null") && VolleyRequest.cookie != null && !VolleyRequest.cookie.equals("")){
            fetchSubreddits(after);
        }else{
            writeSubreddits();
        }
    }

    public void writeSubreddits(){
        final ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, subreddits));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(subreddits.get(position).equals("Search")){
                    // close the drawer
                    mDrawerList.setItemChecked(position, true);
                    mDrawerLayout.closeDrawer(mDrawerList);

                    // Go to the subreddit search page
                    Intent intent = new Intent(context, SubredditSearch.class);
                    context.startActivity(intent);
                } else {
                    if(subreddits.get(position).equals("All")){
                        subreddit = "";
                    } else {
                        subreddit = subreddits.get(position);
                    }

                    listStories.clear();

                    // Highlight the selected item, update the title, and close the drawer
                    mDrawerList.setItemChecked(position, true);
                    setTitle(subreddits.get(position));
                    mDrawerLayout.closeDrawer(mDrawerList);

                    // Update the list view
                    articleAdapter.notifyDataSetChanged();

                    progressbar.setVisibility(View.VISIBLE);
                    loadMore();
                }
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    public void initDrawer(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ){};

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        VolleyRequest.initQueue(this.getApplication());

        // Only load HD thumbnails when connected to wi-fi
        VolleyRequest.loadHdThumbnails = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        context = this;
        listViewStories = (ExpandableListView) findViewById(R.id.listViewStories);
        progressbar = (ProgressBar) findViewById(R.id.progressBarArticles);
        initDrawer();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

            subreddit = "";
            subreddits.add("Search");
            subreddits.add("All");
            loadMore();
            fetchSubreddits("");
        } else {
            listStories = (ArrayList<Article>)savedInstanceState.getSerializable("listStories");
            ArrayList<String> savedSubreddits = (ArrayList<String>)savedInstanceState.getSerializable("subreddits");
            subreddits.addAll(savedSubreddits);
            addStories("");
            writeSubreddits();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.front_page, menu);
        MenuItem logout = menu.findItem(R.id.action_logout);
        MenuItem login = menu.findItem(R.id.action_login);
        MenuItem user = menu.findItem(R.id.action_user);
        MenuItem settings = menu.findItem(R.id.action_settings);

        settings.setVisible(false);

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
                Intent intent = new Intent(context, Login.class);
                context.startActivity(intent);
                break;
            }
            case R.id.action_user:{
                Intent intent = new Intent(context, User.class);
                context.startActivity(intent);
                break;
            }
        }

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable("listStories", listStories);
        outState.putSerializable("subreddits", subreddits);
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
