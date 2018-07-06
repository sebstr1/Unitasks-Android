package com.sest1601.bathingsites.weather;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.sest1601.bathingsites.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

// Class to handle Async downloading & unzipping
public class AsyncGetWeather extends AsyncTask<Void, Integer, Pair<String, Drawable>> {

    // declaring a listener instance
    private OnFetchFinishedListener listener;
    private WeakReference<View> pView;
    private String location;
    private Drawable drawableFromStream;

    // getting a listener instance from the constructor
    public AsyncGetWeather( View v, String loc, OnFetchFinishedListener li) {
        pView = new WeakReference<> (v);
        location = loc;
        listener = li;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pView.get().setVisibility(View.VISIBLE);
    }


    // the listener interface
    public interface OnFetchFinishedListener {
        void onFetchFinished(Pair<String, Drawable> result);
    }

    @Override
    protected Pair<String, Drawable> doInBackground(Void... voids) {

        String result = "";
        try {

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(pView.get().getContext());
            String source = sharedPref.getString(pView.get().getContext().getResources().getString(R.string.pref_key_weatherSource), "null");

            URL url = new URL(source + "?location=" + location + "&language=SE");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int requestLength = connection.getContentLength();
            InputStream inStream;
            inStream = connection.getInputStream();

            long total = 0;
            int count;

            StringBuilder sb = new StringBuilder();
            while ((count = inStream.read()) != -1) {
                if(!isCancelled()) {
                    total += count;
                    sb.append((char) count);
                }
            }

            result = sb.toString();


            inStream.close();
            // Try to get image
            URL imgUrl = new URL(getImgURL(result));
            HttpURLConnection imgConnection = (HttpURLConnection) imgUrl.openConnection();
            inStream = imgConnection.getInputStream();
            imgConnection.connect();
            drawableFromStream = Drawable.createFromStream(inStream, null);

        } catch (MalformedURLException e) {
            Log.e("MalFormedURL", "Invalid URL when trying to get weather");
        } catch (IOException e) {
            Log.e("IOException", "IOException when trying to get weather");
        }

        return Pair.create(result, drawableFromStream);
    }

    private String getImgURL(String str) {
        String tmp = str.substring(str.indexOf("image") + 6);
        return tmp.substring(0,tmp.indexOf("<"));
    }

    @Override
    protected void onPostExecute(Pair<String, Drawable> result) {
        pView.get().setVisibility(View.INVISIBLE);
        listener.onFetchFinished(result);
    }
}