package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;

/**
 * Created by smw on 1/2/14.
 */
public class VolleyRequest {
    public static RequestQueue queue;
    public static String cookie;

    public static void initQueue(Context context){
        queue = com.android.volley.toolbox.Volley.newRequestQueue(context);

        if(cookie == null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            cookie = preferences.getString("Cookie", "");
        }
    }

}
