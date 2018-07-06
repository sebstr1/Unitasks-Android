package com.sest1601.bathingsites;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sest1601.bathingsites.database.AsyncDbQuery;
import com.sest1601.bathingsites.database.BathsiteEntity;

import java.util.List;

public class BathingSitesView extends ConstraintLayout {

    private TextView sites_info;
    private int siteCount = 0;

    BathingSitesView(Context context) {
        super(context);
        init(context);
    }

    BathingSitesView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    private void init(Context context) {


        // Attach the XML to the activity (make it visible)
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflated = inflater.inflate(R.layout.bathingsitesview_component, this, true);
        sites_info = inflated.findViewById(R.id.BathingViewNumberOfSites);
        queryDB();
    }

    // Query DB to get count of Bathsites in DB
    public void queryDB() {
        new AsyncDbQuery(getContext(), new AsyncDbQuery.onQueryFinishedListener() {
            @Override
            public void onQueryFinished(List<BathsiteEntity> result) {
                setSiteCount(result.size());
            }
        }).execute();
    }


    public int getSiteCount() {

        return siteCount;
    }

    public void setSiteCount(int siteCount) {
        this.siteCount = siteCount;
        String sites = siteCount + " Bathing Sites";
        sites_info.setText(sites);
    }
}