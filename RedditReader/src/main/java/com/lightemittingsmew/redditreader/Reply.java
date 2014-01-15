package com.lightemittingsmew.redditreader;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Reply extends ActionBarActivity {
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Intent intent = getIntent();
        fullname = intent.getStringExtra(CommentArrayAdapter.PARENT_FULLNAME);

        Button reply = (Button) findViewById(R.id.buttonReply);
        Button cancel = (Button) findViewById(R.id.buttonCancel);
        final EditText editTextReply = (EditText) findViewById(R.id.editTextReply);

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replyText = editTextReply.getText().toString();
                reply(replyText, fullname);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reply, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_reply, container, false);
            return rootView;
        }
    }

    public void reply(final String replyText, final String fullname){
        StringRequest replyRequest = new StringRequest(Request.Method.POST, "http://www.reddit.com/api/comment", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getApplicationContext(), "Post Successful", Toast.LENGTH_SHORT).show();

                finish();
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
                headers.put("User-Agent", "redditReader01");

                return headers;
            }
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("api_type", "json");
                params.put("text", replyText);
                params.put("thing_id", fullname);
                params.put("uh", VolleyRequest.modhash);

                return params;
            }
        };

        VolleyRequest.queue.add(replyRequest);
    }
}
