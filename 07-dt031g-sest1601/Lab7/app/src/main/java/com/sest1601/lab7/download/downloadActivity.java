package com.sest1601.lab7.download;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sest1601.lab7.MainActivity;
import com.sest1601.lab7.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class downloadActivity extends AppCompatActivity {
    private WebView webview;
    private ProgressBar pBar;
    private TextView pText;
    private View pView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting up the view
        setContentView(R.layout.activity_download);
        pBar = findViewById(R.id.progressBar);
        pText = findViewById(R.id.textView);
        pView = findViewById(R.id.view);
        pView.setVisibility(View.INVISIBLE);
        String url = getIntent().getStringExtra(MainActivity.TRANSLATE_JOB);

        // Setting up webview
        webview = findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(url);

        // Set a downloadListener
        webview.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                try {
                    URL dlURL = new URL(url);
                    new DownloadWithProgressbar(pView, pBar, pText).execute(dlURL);
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

    // Class to handle Async downloading & unzipping
    private static class DownloadWithProgressbar extends AsyncTask<URL, Integer, Integer> {

        private static WeakReference<View> pView;
        private static WeakReference<ProgressBar> pBar;
        private static WeakReference<TextView> pText;
        private static String fileName;

        // Constructor that sets up WeakReferences
        DownloadWithProgressbar(View v, ProgressBar b, TextView t) {
            pView = new WeakReference<>(v);
            pBar = new WeakReference<>(b);
            pText = new WeakReference<>(t);
        }

        // Just before executing set up
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pBar.get().setProgress(0);
            pView.get().setVisibility(View.VISIBLE);
            pText.get().setText("");
        }

        // Background thread - Download and unzip
        protected Integer doInBackground(URL... urls) {
            int count;
            // Create Folder if not existing
            try {
                File folder = new File(pBar.get().getContext().getExternalFilesDir(null) + "/sounds");

                if (!folder.exists()) {
                    folder.mkdir();
                }

                // Create URL & HTTP connection
                URL url = urls[0];
                HttpURLConnection conection = (HttpURLConnection) url.openConnection();
                conection.setRequestMethod("GET");
                conection.connect();

                // Useful info to show the 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                fileName = URLUtil.guessFileName(url.toString(), null, null);

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream(folder.getAbsolutePath() + "/" + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    if(!isCancelled()) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress((int) (total * 100 / lenghtOfFile));
                        // writing data to file
                        output.write(data, 0, count);
                    }

                }
                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();
                conection.disconnect();


                // Unzip & delete the zip
                ZIP.decompress(folder.getAbsolutePath() + "/" + fileName, folder.getAbsolutePath());
                File zip = new File(folder.getAbsolutePath() + "/" + fileName);
                zip.delete();
                return null;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }
        // If download canceled
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(pView.get().getContext(), fileName + " Interrupted",
                    Toast.LENGTH_SHORT).show();

        }

        /**
         * Updating progress bar
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {

            String prog = String.valueOf(progress[0]) + "%";
            pText.get().setText(prog);
            pBar.get().setProgress(progress[0]);
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(Integer result) {
            pView.get().setVisibility(View.INVISIBLE);
            Toast.makeText(pView.get().getContext(), fileName + " Downloaded!",
                    Toast.LENGTH_LONG).show();

        }
    }
}

