package com.lightemittingsmew.redditreader;

import android.content.Intent;
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
        final String url = intent.getStringExtra(ArticleArrayAdapter.ARTICLE_URL);

        WebView webView = (WebView) findViewById(R.id.webViewArticle);

        // Enable javascript
        webView.getSettings().setJavaScriptEnabled(true);

        // Allow user to zoom
        webView.getSettings().setBuiltInZoomControls(true);

        // Make it so links open in the same view
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(url);
        addBanner();
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
}
