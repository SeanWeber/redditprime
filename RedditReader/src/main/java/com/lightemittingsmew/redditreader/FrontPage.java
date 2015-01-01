package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FrontPage extends BaseActivity implements ActionBar.TabListener{
    private Context context;
    ExpandableListView listViewStories;
    ArrayList<Article> listStories;
    ArticleArrayAdapter articleAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    String subreddit = "/";
    String sortBy = "";
    ProgressBar progressbar;
    final ArrayList<String> subreddits = new ArrayList<String>();
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

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
        String url = "https://www.reddit.com" + subreddit + sortBy + "/.json";

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
        String url = "https://www.reddit.com/subreddits/.json";

        if(!VolleyRequest.cookie.equals("")){
            url = "https://www.reddit.com/subreddits/mine/subscriber/.json";
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
                        subreddit = subreddits.get(position) + "/";
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
        VolleyRequest.initQueue(this.getApplication());
        style = VolleyRequest.style;
        setTheme(style);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        // Set up the action bar to show tabs.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // for each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText("hot")
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("new")
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("controversial")
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("top")
                .setTabListener(this));

        ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // Decide whether HD thumbnails should be loaded
        if(VolleyRequest.loadHdThumbnailSetting == VolleyRequest.AlwaysLoad){
            VolleyRequest.loadHdThumbnails = true;
        } else if(VolleyRequest.loadHdThumbnailSetting == VolleyRequest.WifiLoad){
            VolleyRequest.loadHdThumbnails = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        } else {
            VolleyRequest.loadHdThumbnails = false;
        }

        context = this;
        listViewStories = (ExpandableListView) findViewById(R.id.listViewStories);
        progressbar = (ProgressBar) findViewById(R.id.progressBarArticles);
        initDrawer();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

            // If we're coming from the Subreddit search activity, set the subreddit
            Intent intent = getIntent();
            if(intent != null){
                final String foundSubreddit = intent.getStringExtra(SubredditArrayAdapter.FOUND_SUBREDDIT);

                if(foundSubreddit != null){
                    subreddit = foundSubreddit;
                }
            }

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
    protected void onResume() {
        super.onResume();
        checkNewMessages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.front_page, menu);

        MenuItem submit = menu.findItem(R.id.action_submit);

        if(VolleyRequest.cookie == null || VolleyRequest.cookie.equals("")){
            submit.setVisible(false);

        } else {
            submit.setVisible(true);
        }

        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit:{
                Intent intent = new Intent(context, Submit.class);
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

        // Serialize the current tab position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
                .getSelectedNavigationIndex());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current tab position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        sortBy = tab.getText().toString();
        if(listStories != null) {
            listStories.clear();
            articleAdapter.notifyDataSetChanged();

            progressbar.setVisibility(View.VISIBLE);
            loadMore();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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

    private void checkNewMessages(){
        final String url = "https://www.reddit.com/message/unread/.json";

        final StringRequest messageRequest = new RedditRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                updateNewMessages(response);
            }
        });

        VolleyRequest.queue.add(messageRequest);
    }

    private void updateNewMessages(String response){
        try {
            JSONArray newMessages = new JSONObject(response).getJSONObject("data").getJSONArray("children");

            if(newMessages.length() > 0){
                VolleyRequest.hasNewMessage = true;
                supportInvalidateOptionsMenu();
            } else {
                VolleyRequest.hasNewMessage = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
