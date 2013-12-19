package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

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
        RequestQueue queue = Volley.newRequestQueue(thisContext);

        LayoutInflater inflater = (LayoutInflater) thisContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_article, parent, false);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewThumbnail);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewTitle);

        String title;
        String thumbNailUrl = "default";
        try {
            title = articles.get(position).getString("title");
            thumbNailUrl = articles.get(position).getString("thumbnail");
        } catch (JSONException e) {
            e.printStackTrace();
            title = "Could not find title";
        }
        textView.setText(title);

        if(thumbNailUrl.equals("default") || thumbNailUrl.equals("")){}
        if(thumbNailUrl.equals("self")){}
        if(thumbNailUrl.equals("nsfw")){}
        else{
            try {
                ImageRequest ir = new ImageRequest(thumbNailUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, null, null);

                queue.add(ir);
            }
            catch (NullPointerException e){
                e.printStackTrace();
                Log.e("Problem URL", thumbNailUrl);
            }
        }

        return rowView;
    }
}
