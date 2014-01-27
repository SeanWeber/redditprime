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
    public static ImageLoader imageLoader;
    public static boolean loadHdThumbnails;

    public static void initQueue(Context context){
        queue = com.android.volley.toolbox.Volley.newRequestQueue(context);

        if(cookie == null || modhash == null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            cookie = preferences.getString("Cookie", "");
            modhash = preferences.getString("Modhash", "");
        }

        imageLoader = new ImageLoader(VolleyRequest.queue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(25);

            @Override
            public void putBitmap(String key, Bitmap value) {
                mCache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return mCache.get(key);
            }
        });
    }

    public static void vote(final String voteDirection, final String fullname){
        StringRequest upvoteRequest = new StringRequest(Request.Method.POST, "http://www.reddit.com/api/vote", new Response.Listener<String>() {
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
                headers.put("User-Agent", "redditReader01");

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

        // Save the cookie so the user does not need to log in each time
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString("Cookie", cookie);
        prefEditor.putString("Modhash", modhash);
        prefEditor.commit();
    }
}
