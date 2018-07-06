package com.sest1601.lab7.history;

import com.sest1601.lab7.MainActivity;
import com.sest1601.lab7.R;
import com.sest1601.lab7.database.HistoryDB;
import com.sest1601.lab7.database.HistoryEntity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private TextView noResultText;
    private ListView resultList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        init();
    }

    private void init() {
        setTitle("Call history");
        noResultText = findViewById(R.id.noHistoryText);
        resultList = findViewById(R.id.historyList);

        // Get history from DB via Asynctask
        new AsyncDbCall(new AsyncDbCall.OnFetchFinishedListener() {
            @Override
            public void onFetchFinished(List<HistoryEntity> result) {
                // Result from the AsyncTask (List of history Entities)
               printHistory(result);
            }
        }).execute();
    }


    private void printHistory(List<HistoryEntity> historyList) {

        // If list is empty show the No result text
        if (historyList.isEmpty()) {
            noResultText.setVisibility(View.VISIBLE);
        }

        else {
            // Sort by date
            Collections.sort(historyList, new Comparator<HistoryEntity>() {
                public int compare(HistoryEntity o1, HistoryEntity o2) {
                    if (o1.getDate() == null || o2.getDate() == null)
                        return 0;
                    return o1.compDate().compareTo(o2.compDate());
                }
            });

            HistoryAdapter adapter = new HistoryAdapter(this, historyList);
            resultList.setAdapter(adapter);
            noResultText.setVisibility(View.INVISIBLE);
            resultList.setVisibility(View.VISIBLE);
        }
    }
}
