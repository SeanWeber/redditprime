package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Reply extends BaseActivity {
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
        final Context currentContext = this;
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replyText = editTextReply.getText().toString();
                reply(replyText, fullname, currentContext);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
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

    public void reply(final String replyText, final String fullname, final Context context){
        StringRequest replyRequest = new StringRequest(Request.Method.POST, "https://www.reddit.com/api/comment", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("CommentReply", s);

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
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", fullname);
                    setResult(RESULT_OK,returnIntent);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Error occurred while posting", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();

                if (headers == null || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }

                headers.put("Cookie", VolleyRequest.cookie);
                headers.put("User-Agent", VolleyRequest.APP_VERSION);

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
