package com.lightemittingsmew.redditreader;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Submit extends ActionBarActivity {
    Button textButton;
    Button linkButton;
    Button submitButton;

    TextView urlTitle;
    EditText urlText;

    TextView textTitle;
    EditText textText;

    Boolean isTextPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        textButton = (Button) findViewById(R.id.buttonText);
        linkButton = (Button) findViewById(R.id.buttonLink);
        submitButton = (Button) findViewById(R.id.buttonSubmitPost);

        urlTitle = (TextView) findViewById(R.id.textViewUrl);
        urlText = (EditText) findViewById(R.id.editTextUrl);

        textTitle = (TextView) findViewById(R.id.textViewText);
        textText = (EditText) findViewById(R.id.editTextText);

        buttonLinkClick(linkButton);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.submit, menu);
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

    public void buttonLinkClick(View view){
        urlTitle.setVisibility(View.VISIBLE);
        urlText.setVisibility(View.VISIBLE);

        textTitle.setVisibility(View.GONE);
        textText.setVisibility(View.GONE);

        isTextPost = false;
    }

    public void buttonTextClick(View view){
        urlTitle.setVisibility(View.GONE);
        urlText.setVisibility(View.GONE);

        textTitle.setVisibility(View.VISIBLE);
        textText.setVisibility(View.VISIBLE);

        isTextPost = true;
    }

    public void buttonSubmitClick(View view){
        EditText editTextchooseSubreddit = (EditText) findViewById(R.id.editTextChoseSubreddit);
        EditText editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        CheckBox checkBoxSendReplies = (CheckBox) findViewById(R.id.checkBoxSendReplies);

        String kind;
        String text = "";
        String url = "";

        String subreddit = editTextchooseSubreddit.getText().toString();
        String title = editTextTitle.getText().toString();
        Boolean sendReplies = checkBoxSendReplies.isChecked();

        if(isTextPost){
            kind = "self";
            text = textText.getText().toString();
        } else {
            kind = "link";
            url = urlText.getText().toString();
        }

        submit(kind, sendReplies, subreddit, text, title, url);
    }

    public void submit(final String kind, final Boolean sendReplies, final String subreddit, final String text, final String title, final String url){
        StringRequest submitRequest = new StringRequest(Request.Method.POST, "http://www.reddit.com/api/submit", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("SubmitPost", s);

                String errors = "";
                try {
                    errors = new JSONObject(s).getJSONObject("json").getJSONArray("errors").getJSONArray(0).getString(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Show the user whether the post succeeded
                if(!errors.equals("")){
                    Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Post Successful", Toast.LENGTH_SHORT).show();

                    // Return to the previous activity
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
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
                params.put("kind", kind);
                params.put("sendreplies", sendReplies.toString());
                params.put("sr", subreddit);
                params.put("title", title);
                params.put("uh", VolleyRequest.modhash);

                if(kind.equals("self")){
                    params.put("text", text);
                } else {
                    params.put("url", url);
                }

                return params;
            }
        };

        VolleyRequest.queue.add(submitRequest);
    }
}
