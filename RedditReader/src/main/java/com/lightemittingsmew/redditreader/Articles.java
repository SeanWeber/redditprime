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

        webView.loadUrl(url);
        addBanner();
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

    private void addBanner(){
        // Create the adView
        AdView adView = new AdView(this, AdSize.SMART_BANNER, "ca-app-pub-7856499565563098/6256720366");

        // Lookup your LinearLayout assuming it's been given
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutAd);

        // Add the adView to it
        layout.addView(adView);

        // Initiate a generic request to load it with an ad
        AdRequest adRequest = new AdRequest();

        // Show dummy ads for test devices so we stay in good standing with admob
        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        adRequest.addTestDevice("3806E58F6A2D26FBAF4B4B1A6F4DE519");
        adRequest.addTestDevice("1DEEB5FDAF02CD82E12A109A8A6D357E");
        adRequest.addTestDevice("FAD5C2235FFBABDE132C75EBBBE6BB38");
        adRequest.addTestDevice("6C5412BF56EEBA7B0C01CBA41120B1E6");

        adRequest.addTestDevice("68531784152AD12BCF884D1D4106EC76");
        adRequest.addTestDevice("8E0C2ABC6456B8E2E3F13DBDF44E4D83");

        adRequest.addTestDevice("78FB22283FE01E5A9C965A552245DAB5"); // Galaxy S4
        adView.loadAd(adRequest);
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
