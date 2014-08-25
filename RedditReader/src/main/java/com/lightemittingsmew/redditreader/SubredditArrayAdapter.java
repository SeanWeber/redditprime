package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by smw on 8/24/14.
 */
public class SubredditArrayAdapter extends ArrayAdapter<SubredditResult>{
    private ArrayList<SubredditResult> subreddits;
    LayoutInflater inflater;

    public SubredditArrayAdapter(Context context, int textViewResourceId, ArrayList<SubredditResult> objects){
        super(context, textViewResourceId, objects);

        subreddits = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the proper views and layouts
        final SubredditResult subreddit = subreddits.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_subreddit, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.textViewSubredditName);
        TextView description = (TextView) convertView.findViewById(R.id.textViewSubredditDescription);

        name.setText(subreddit.getName());
        description.setText(subreddit.getDescription());

        return convertView;
    }
}

