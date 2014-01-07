package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by smw on 1/6/14.
 */
public class LoginRequest extends StringRequest {
    private String user;
    private String pass;
    private Context context;

    public LoginRequest(String username, String password, Context con, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, "http://www.reddit.com/api/login", listener, errorListener);
        user = username;
        pass = password;
        context = con;
    }

    @Override
    protected Map<String,String> getParams(){
        Map<String,String> params = new HashMap<String, String>();
        params.put("api_type", "json");
        params.put("passwd", pass);
        params.put("rem", "true");
        params.put("user", user);

        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type","application/x-www-form-urlencoded");
        return params;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        if (response.headers.containsKey("Set-Cookie") && response.headers.get("Set-Cookie").startsWith("reddit_session")) {
            String cookie = response.headers.get("Set-Cookie");
            VolleyRequest.cookie = cookie;

            // Save the cookie so the user does not need to log in each time
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putString("Cookie", cookie);
            prefEditor.commit();
        }

        return super.parseNetworkResponse(response);
    }
}
