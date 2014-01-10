package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by smw on 1/2/14.
 */
public class VolleyRequest {
    public static RequestQueue queue;
    public static String cookie;
    public static String modhash;
    public static ImageLoader imageLoader;

    public static void initQueue(Context context){
        queue = com.android.volley.toolbox.Volley.newRequestQueue(context);

        if(cookie == null || modhash == null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            cookie = preferences.getString("Cookie", "");
            modhash = preferences.getString("Modhash", "");
        }

        imageLoader = new ImageLoader(VolleyRequest.queue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);

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
}
