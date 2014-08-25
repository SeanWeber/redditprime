package com.lightemittingsmew.redditreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

/**
 * Created by smw on 8/24/14.
 */
public class SubredditArrayAdapter extends ArrayAdapter<SubredditResult>{
    public static final String FOUND_SUBREDDIT = "com.lightemittingsmew.redditreader.COMMENT_URL";
    private List<SubredditResult> subreddits;
    LayoutInflater inflater;
    Context context;

    public SubredditArrayAdapter(Context thisContext, int textViewResourceId, List<SubredditResult> objects){
        super(thisContext, textViewResourceId, objects);
        context = thisContext;
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

        LinearLayout listing = (LinearLayout) convertView.findViewById(R.id.layoutSubreddit);
        TextView name = (TextView) convertView.findViewById(R.id.textViewSubredditName);
        TextView description = (TextView) convertView.findViewById(R.id.textViewSubredditDescription);

        // Display info on the subreddit
        name.setText(subreddit.getName());
        description.setText(subreddit.getDescription());

        // When a subreddit is clicked, navigate to that subreddit
        View.OnClickListener goToSubreddit = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String subredditName = subreddit.getName();

                Intent intent = new Intent(context, FrontPage.class);
                intent.putExtra(FOUND_SUBREDDIT, subredditName);
                context.startActivity(intent);
            }
        };
        listing.setOnClickListener(goToSubreddit);

        return convertView;
    }
}

