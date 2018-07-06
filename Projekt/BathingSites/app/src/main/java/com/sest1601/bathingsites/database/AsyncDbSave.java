package com.sest1601.bathingsites.database;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RatingBar;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


// Class to handle Async downloading & unzipping
public class AsyncDbSave extends AsyncTask<Void, Integer, Boolean> {

    // declaring a listener instance
    private OnFetchFinishedListener listener;
    private ArrayList<EditText> textFields;
    private WeakReference<RatingBar> rBar;

    // getting a listener instance from the constructor
    public AsyncDbSave(ArrayList<EditText> arrayList, RatingBar rBar, OnFetchFinishedListener li) {
        textFields = arrayList;
        listener = li;
        this.rBar = new WeakReference<>(rBar);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    // the listener interface
    public interface OnFetchFinishedListener {
        void onFetchFinished(Boolean result);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        String lat = "";
        String lng = "";

        BathsiteEntity site = new BathsiteEntity();
        for (int i = 0; i < textFields.size(); i++) {
            if (i == 0 && notEmpty(textFields.get(i))) {
                site.setName(textFields.get(i).getText().toString());
                site.setRating(rBar.get().getRating());
            }
            if (i == 1 && notEmpty(textFields.get(i))) {
                site.setDescription(textFields.get(i).getText().toString());
            }
            if (i == 2 && notEmpty(textFields.get(i))) {
                site.setAddress(textFields.get(i).getText().toString());
            }
            if (i == 3) {
                if (notEmpty(textFields.get(i))) {
                    site.setLng(textFields.get(i).getText().toString());
                    lng = textFields.get(i).getText().toString();
                } else {
                    site.setLng(null);
                }
            }
            if (i == 4) {
                if (notEmpty(textFields.get(i))) {
                    site.setLat(textFields.get(i).getText().toString());
                    lat = textFields.get(i).getText().toString();
                } else {
                    site.setLat(null);
                }
            }
            if (i == 5 && notEmpty(textFields.get(i))) {
                site.setWatertemp(textFields.get(i).getText().toString());
            }
            if (i == 6 && notEmpty(textFields.get(i))) {
                site.setWatertempdate(textFields.get(i).getText().toString());
            }
        }

        BathsiteDB db = BathsiteDB.getInstance(rBar.get().getContext());

        // If long lat combo is
        if (!(db.dao().siteExists(lng, lat) > 0)) {
            try {
                db.dao().insert(site);
            } catch (SQLiteConstraintException e) {
                Log.e("SQLiteException", "Site already exists");
                return false;
            }
        } else {
            Log.e("SQLiteException", "Site already exists");
            return false;
        }
        return true;
    }


    // Checks if textfield is empty
    private boolean notEmpty(EditText field) {
        if (TextUtils.isEmpty(field.getText())) {
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onPostExecute(Boolean OperationResult) {
        listener.onFetchFinished(OperationResult);
    }





}