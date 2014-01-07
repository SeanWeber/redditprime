package com.lightemittingsmew.redditreader;

import android.content.Context;

import com.android.volley.RequestQueue;

/**
 * Created by smw on 1/2/14.
 */
public class VolleyRequest {
    public static RequestQueue queue;
    public static String cookie;

    public static void initQueue(Context context){
        queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
    }


}
