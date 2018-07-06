package com.sest1601.lab7.history;

import android.os.AsyncTask;

import com.sest1601.lab7.MainActivity;
import com.sest1601.lab7.database.HistoryEntity;

import java.util.List;

// Class to handle Async downloading & unzipping
public class AsyncDbCall extends AsyncTask<Void, Void, List<HistoryEntity>> {

    // declaring a listener instance
    private OnFetchFinishedListener listener;

    // getting a listener instance from the constructor
    public AsyncDbCall(OnFetchFinishedListener listener) {
        this.listener = listener;
    }


    // the listener interface
    public interface OnFetchFinishedListener {
        void onFetchFinished(List<HistoryEntity> result);
    }

    @Override
    protected List<HistoryEntity> doInBackground(Void... voids) {
        List<HistoryEntity> result = MainActivity.DB.historyDao().getAll();
        return result;
    }

    @Override
    protected void onPostExecute(List<HistoryEntity> result) {
        listener.onFetchFinished(result);
    }
}