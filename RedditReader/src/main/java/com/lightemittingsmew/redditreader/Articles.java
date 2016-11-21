package com.lightemittingsmew.redditreader;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class Articles extends BaseActivity {

    public static final String ARTICLE_URL   = "com.lightemittingsmew.redditreader.ARTICLE_URL";
    public static final String ARTICLE_TITLE = "com.lightemittingsmew.redditreader.ARTICLE_TITLE";
    String url;
    String title;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Intent intent = getIntent();
        url = intent.getStringExtra(Articles.ARTICLE_URL);
        title = intent.getStringExtra(Articles.ARTICLE_TITLE);

        if(title != null){
            setTitle(title);
        }

        webView = (WebView) findViewById(R.id.webViewArticle);

        // Enable javascript
        webView.getSettings().setJavaScriptEnabled(true);

        // Allow user to zoom
        webView.getSettings().setBuiltInZoomControls(true);

        // Make it so links open in the same view
        webView.setWebViewClient(new WebViewClient());

        // This is necessary to load imgur albums
        webView.getSettings().setDomStorageEnabled(true);
        
        webView.loadUrl(url);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // The webview thread must be manually closed
            webView.onPause();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_article, container, false);
            return rootView;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article, menu);

        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_launch_browser:{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
