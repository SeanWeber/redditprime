package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smw on 1/2/14.
 */
public class VolleyRequest {
    public static RequestQueue queue;
    public static String cookie;
    public static String modhash;
    public static String user;
    public static ImageLoader imageLoader;
    public static boolean loadHdThumbnails;
    public static boolean hasNewMessage;
    public static boolean disableScoreColor;
    public static final String APP_VERSION = "redditReader0.3 by smew";
    public static final String EMPTY_STRING = "";

    public static int loadHdThumbnailSetting;
    public static final int AlwaysLoad = 0;
    public static final int WifiLoad   = 1;
    public static final int NeverLoad  = 2;

    public static int style = R.style.Dark;

    public static void initQueue(Context context){
        queue = com.android.volley.toolbox.Volley.newRequestQueue(context);

        if(cookie == null || modhash == null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            cookie = preferences.getString("Cookie", "");
            modhash = preferences.getString("Modhash", "");
            user = preferences.getString("User", "");

            loadHdThumbnailSetting = preferences.getInt("LoadHdThumbnailSetting", WifiLoad);
            style = preferences.getInt("Style", R.style.Dark);
            disableScoreColor = preferences.getBoolean("DisableScoreColor", false);
        }

        imageLoader = new ImageLoader(VolleyRequest.queue, new BitmapLruCache());
    }

    public static void vote(final String voteDirection, final String fullname){
        StringRequest upvoteRequest = new StringRequest(Request.Method.POST, "https://www.reddit.com/api/vote", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

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

                headers.put("Cookie", cookie);
                headers.put("User-Agent", APP_VERSION);

                return headers;
            }
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("dir", voteDirection);
                params.put("id", fullname);
                params.put("uh", modhash);

                return params;
            }
        };

        queue.add(upvoteRequest);
    }

    public static void logout(Context context){
        cookie = "";
        modhash = "";
        user = "";

        // Delete the user's credentials
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString("Cookie", EMPTY_STRING);
        prefEditor.putString("Modhash", EMPTY_STRING);
        prefEditor.putString("User", EMPTY_STRING);
        prefEditor.commit();
    }
}