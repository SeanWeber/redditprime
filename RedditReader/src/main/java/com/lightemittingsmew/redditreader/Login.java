package com.lightemittingsmew.redditreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void buttonLoginClick(View view){
        EditText username = (EditText) findViewById(R.id.editTextUsername);
        EditText password = (EditText) findViewById(R.id.editTextPassword);

        final String user = username.getText().toString();
        final String pass = password.getText().toString();

        LoginRequest loginRequest = new LoginRequest (user, pass, this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String cookie = "";
                String modhash = "";

                try {
                    JSONObject jso = new JSONObject(response);
                    cookie = "reddit_session=" + jso.getJSONObject("json").getJSONObject("data").getString("cookie");
                    modhash = jso.getJSONObject("json").getJSONObject("data").getString("modhash");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                VolleyRequest.cookie = cookie;
                VolleyRequest.modhash = modhash;
                VolleyRequest.user = user;

                // Save the cookie so the user does not need to log in each time
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor prefEditor = preferences.edit();
                prefEditor.putString("Cookie", cookie);
                prefEditor.putString("Modhash", modhash);
                prefEditor.putString("User", user);
                prefEditor.commit();

                login();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyRequest.queue.add(loginRequest);
    }

    private void login(){
        Intent intent = new Intent(this, FrontPage.class);
        this.startActivity(intent);
    }
}
