package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import java.util.List;

/**
 * Created by smw on 12/19/13.
 */
public class ArticleArrayAdapter extends BaseExpandableListAdapter {
    public static final String COMMENT_URL = "com.lightemittingsmew.redditreader.COMMENT_URL";
    private Context thisContext;
    private List<Article> articles;
    private FrontPage activity;

    public ArticleArrayAdapter(Context context, List<Article> objects, FrontPage act) {
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

        if(articles.size() == 0){
            return convertView;
        }

        Article currentArticle = articles.get(groupPosition);
        String thumbnailUrl = currentArticle.getThumbnail();
        String title = currentArticle.getTitle();
        String score = String.format("%s points by %s %s<br>%d comments in /r/%s",
                                     currentArticle.getScore(),
                                     currentArticle.getAuthor(),
                                     currentArticle.timeAgo(),
                                     currentArticle.getComments(),
                                     currentArticle.getSubreddit());

        holder.textListChild.setText(title);
        holder.textViewScore.setText(Html.fromHtml(score));

        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.imageView.setBackgroundColor(Color.parseColor("#336699"));
        holder.imageView.setDefaultImageResId(R.drawable.rr5);
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

        if(articles.size() == 0){
            return convertView;
        }

        final Article openedArticle = articles.get(groupPosition);
        final ArticleArrayAdapter adapter = this;
        TextView txtListChild = (TextView) convertView.findViewById(R.id.textViewChild);
        ImageButton buttonComment = (ImageButton) convertView.findViewById(R.id.buttonComments);
        ImageButton buttonArticle = (ImageButton) convertView.findViewById(R.id.buttonArticle);
        ImageButton buttonUpvote = (ImageButton) convertView.findViewById(R.id.buttonUpvote);
        ImageButton buttonDownvote = (ImageButton) convertView.findViewById(R.id.buttonDownvote);

        String commentUrl = openedArticle.getPermalink();
        String articleUrl = openedArticle.getUrl();

        if(openedArticle.isSelf()){
            SpannableStringBuilder ssb;
            txtListChild.setVisibility(View.VISIBLE);

            txtListChild.setText(openedArticle.getParsedBody(thisContext));
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

        if(openedArticle.isUpvoted()){
            buttonUpvote.setImageResource(R.drawable.upvoteactive);
        } else{
            buttonUpvote.setImageResource(R.drawable.upvote);
        }

        if(openedArticle.isDownvoted()){
            buttonDownvote.setImageResource(R.drawable.downvoteactive);
        } else{
            buttonDownvote.setImageResource(R.drawable.downvote);
        }

        final String finalUrl = "https://www.reddit.com" + commentUrl + ".json";
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
                intent.putExtra(Articles.ARTICLE_URL, finalArticleUrl);
                thisContext.startActivity(intent);
            }
        });

        buttonUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openedArticle.upVote();
                adapter.notifyDataSetChanged();
            }
        });

        buttonDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openedArticle.downVote();
                adapter.notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
