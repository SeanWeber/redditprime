package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smw on 12/19/13.
 */
public class ArticleArrayAdapter extends ArrayAdapter<JSONObject> {
    private Context thisContext;
    private ArrayList<JSONObject> articles;

    public ArticleArrayAdapter(Context context, int textViewResourceId,
                               ArrayList<JSONObject> objects) {
        super(context, textViewResourceId, objects);
        thisContext = context;
        articles = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) thisContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_article, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewTitle);
        String title = "Could not find title";
        String thumbnailUrl = "default";

        try {
            title = articles.get(position).getString("title");
            thumbnailUrl = articles.get(position).getString("thumbnail");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView.setText(title);

        ImageLoader imageLoader = new ImageLoader(FrontPage.queue, new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) { }

            @Override
            public Bitmap getBitmap(String key) {
                return null;
            }
        });

        if(thumbnailUrl.equals("default") || thumbnailUrl.equals("")){}
        else if(thumbnailUrl.equals("self")){}
        else if(thumbnailUrl.equals("nsfw")){}
        else{
            NetworkImageView imageViewThumbnail = (NetworkImageView)rowView.findViewById(R.id.imageViewThumbnail);
            try{
                imageViewThumbnail.setImageUrl(thumbnailUrl, imageLoader);
            }
            catch(NullPointerException e){
                e.printStackTrace();
                Log.e("URL", thumbnailUrl);
            }
        }

        return rowView;
    }
}
