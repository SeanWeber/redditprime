package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smw on 12/19/13.
 */
public class ArticleArrayAdapter extends BaseExpandableListAdapter {
    public static final String COMMENT_URL = "com.lightemittingsmew.redditreader.COMMENT_URL";
    private Context thisContext;
    private ArrayList<JSONObject> articles;

    public ArticleArrayAdapter(Context context, int textViewResourceId, ArrayList<JSONObject> objects) {
        thisContext = context;
        articles = objects;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) thisContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_article, parent, false);
        }

        String title = "no";
        String thumbnailUrl = "default";

        TextView txtListChild = (TextView) convertView.findViewById(R.id.textViewTitle);
        NetworkImageView imageViewThumbnail = (NetworkImageView) convertView.findViewById(R.id.imageViewThumbnail);

        try {
            title = articles.get(groupPosition).getString("title");
            thumbnailUrl = articles.get(groupPosition).getString("thumbnail");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtListChild.setText(title);

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
            try{
                imageViewThumbnail.setImageUrl(thumbnailUrl, imageLoader);
            }
            catch(NullPointerException e){
                e.printStackTrace();
                Log.e("URL", thumbnailUrl);
            }
        }

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return articles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) thisContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_article_child, parent, false);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.textViewChild);
        Button buttonComment = (Button) convertView.findViewById(R.id.buttonComments);
        txtListChild.setText("childText");

        String url = "nope";

        try {
            url = articles.get(groupPosition).getString("permalink");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalUrl = url;
        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext, Comments.class);
                intent.putExtra(COMMENT_URL, finalUrl);
                thisContext.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
