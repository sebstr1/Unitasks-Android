package com.sest1601.bathingsites.database;


import android.content.Context;
import android.os.AsyncTask;
import java.lang.ref.WeakReference;
import java.util.List;


// Class to handle Async Queries to DB
public class AsyncDbQuery extends AsyncTask<Void, Integer, List<BathsiteEntity>> {

    // declaring a listener instance
    private onQueryFinishedListener listener;
    private WeakReference<Context> c;

    // getting a listener instance from the constructor
    public AsyncDbQuery(Context c, onQueryFinishedListener li) {
        listener = li;
        this.c = new WeakReference<>(c);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    // the listener interface
    public interface onQueryFinishedListener {
        void onQueryFinished(List<BathsiteEntity> result);
    }

    // Query to get all Sites from the DB
    @Override
    protected List<BathsiteEntity> doInBackground(Void... voids) {
        return BathsiteDB.getInstance(c.get()).dao().getAll();
    }

    // Return List of all bathsites from db
    @Override
    protected void onPostExecute(List<BathsiteEntity> result) {
        listener.onQueryFinished(result);
    }

}