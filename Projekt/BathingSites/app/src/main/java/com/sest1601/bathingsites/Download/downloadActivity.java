package com.sest1601.bathingsites.Download;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sest1601.bathingsites.R;

import java.net.MalformedURLException;
import java.net.URL;

public class downloadActivity extends AppCompatActivity {
    private WebView webview;
    private View pView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting up the view
        setContentView(R.layout.activity_download);

        pView = findViewById(R.id.Progressview);
        pView.setVisibility(View.INVISIBLE);

        // Setting up webview
        webview = findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());



        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String source = sharedPref.getString(getString(R.string.pref_key_downloadSource), null);
        webview.loadUrl(source);

        // Set a downloadListener
        webview.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                try {
                    URL dlURL = new URL(url);
                    new AsyncDownload(pView).execute(dlURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Enable back button
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
