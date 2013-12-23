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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FrontPage extends ActionBarActivity {

    public static RequestQueue queue;
    public static final String COMMENT_URL = "com.lightemittingsmew.redditreader.COMMENT_URL";

    private void displayStories(JSONObject stories){
        ListView listViewStories = (ListView)findViewById(R.id.listViewStories);
        ArrayList<JSONObject> listStories = new ArrayList<JSONObject>();

        try {
            JSONArray jsonArrayStories = stories.getJSONObject("data").getJSONArray("children");

            for(int i=0;i<jsonArrayStories.length();i++){
                JSONObject currentStory = jsonArrayStories.getJSONObject(i).getJSONObject("data");
                listStories.add(currentStory);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter articleAdapter = new ArticleArrayAdapter(this, R.layout.list_article, listStories);
        listViewStories.setAdapter(articleAdapter);

        listViewStories.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                JSONObject item = (JSONObject)parent.getItemAtPosition(position);
                String url = "nope";
                try {
                    url = item.getString("permalink");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), Comments.class);
                intent.putExtra(COMMENT_URL, url);
                startActivity(intent);
            }
        });
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

        queue = Volley.newRequestQueue(this);
        String url = "http://www.reddit.com/.json";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                displayStories(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        queue.add(jsObjRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.front_page, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_front_page, container, false);
            return rootView;
        }
    }

}
