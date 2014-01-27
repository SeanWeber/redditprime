package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by smw on 12/19/13.
 */
public class ArticleArrayAdapter extends BaseExpandableListAdapter {
    public static final String COMMENT_URL = "com.lightemittingsmew.redditreader.COMMENT_URL";
    public static final String ARTICLE_URL = "com.lightemittingsmew.redditreader.ARTICLE_URL";
    private Context thisContext;
    private ArrayList<Article> articles;
    private FrontPage activity;

    public ArticleArrayAdapter(Context context, ArrayList<Article> objects, FrontPage act) {
        thisContext = context;
        articles = objects;
        activity = act;
    }

    private static class ViewHolder {
        private NetworkImageView imageView;
        private TextView textListChild;
        private TextView textViewScore;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) thisContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_article, parent, false);

            holder = new ViewHolder();
            holder.imageView = (NetworkImageView) convertView.findViewById(R.id.imageViewThumbnail);
            holder.textListChild = (TextView) convertView.findViewById(R.id.textViewTitle);
            holder.textViewScore = (TextView) convertView.findViewById(R.id.textViewScore);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Article currentArticle = articles.get(groupPosition);
        String thumbnailUrl = currentArticle.getThumbnail();
        String title = currentArticle.getTitle();
        String score = "<font color='#666666'>" + currentArticle.getScore() +
                " points by " + currentArticle.getAuthor() + " in /r/" +
                currentArticle.getSubreddit() + "</font>";

        holder.textListChild.setText(title);
        holder.textViewScore.setText(Html.fromHtml(score));

        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.imageView.setBackgroundColor(Color.parseColor("#336699"));
        holder.imageView.setDefaultImageResId(R.drawable.rr5);
        holder.imageView.setErrorImageResId(R.drawable.errorthumbnail);
        holder.imageView.setImageUrl(null, VolleyRequest.imageLoader);

        if(thumbnailUrl.equals("default") || thumbnailUrl.equals("")){}
        else if(thumbnailUrl.equals("self")){}
        else if(thumbnailUrl.equals("nsfw")){}
        else if(currentArticle.isImage() && VolleyRequest.loadHdThumbnails){
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imageView.setImageUrl(currentArticle.getUrl(), VolleyRequest.imageLoader);
        }
        else{
            try{
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.imageView.setImageUrl(thumbnailUrl, VolleyRequest.imageLoader);
            }
            catch(Exception e){
                e.printStackTrace();
                Log.e("URL", thumbnailUrl);
            }
        }

        if(groupPosition == articles.size() -1){
            activity.loadMore();
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
            LayoutInflater inflater = (LayoutInflater) thisContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_article_child, parent, false);
        }

        final Article openedArticle = articles.get(groupPosition);
        TextView txtListChild = (TextView) convertView.findViewById(R.id.textViewChild);
        Button buttonComment = (Button) convertView.findViewById(R.id.buttonComments);
        Button buttonArticle = (Button) convertView.findViewById(R.id.buttonArticle);
        Button buttonUpvote = (Button) convertView.findViewById(R.id.buttonUpvote);
        Button buttonDownvote = (Button) convertView.findViewById(R.id.buttonDownvote);

        String commentUrl = openedArticle.getPermalink();
        String articleUrl = openedArticle.getUrl();

        if(openedArticle.isSelf()){
            txtListChild.setVisibility(View.VISIBLE);
            txtListChild.setText(Html.fromHtml(Html.fromHtml(openedArticle.getSelftext()).toString()));
            txtListChild.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable
        } else {
            txtListChild.setText("");
            txtListChild.setVisibility(View.GONE);
        }

        NetworkImageView img = (NetworkImageView) convertView.findViewById(R.id.imageViewfull);
        img.setImageUrl(null, VolleyRequest.imageLoader);

        if(openedArticle.isImage()){
            img.setImageUrl(articleUrl, VolleyRequest.imageLoader);
            img.setAdjustViewBounds(true);
        } else {
            img.setAdjustViewBounds(false);
        }

        final String finalUrl = commentUrl;
        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Article.setCurrentArticle(openedArticle);
                Intent intent = new Intent(thisContext, Comments.class);
                intent.putExtra(COMMENT_URL, finalUrl);
                thisContext.startActivity(intent);
            }
        });

        final String finalArticleUrl = articleUrl;
        buttonArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext, Articles.class);
                intent.putExtra(ARTICLE_URL, finalArticleUrl);
                thisContext.startActivity(intent);
            }
        });

        buttonUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openedArticle.upVote();
            }
        });

        buttonDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openedArticle.downVote();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
