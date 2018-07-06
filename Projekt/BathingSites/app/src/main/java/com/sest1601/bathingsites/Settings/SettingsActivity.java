package com.sest1601.bathingsites.Settings;

import android.app.Activity;
import android.os.Bundle;

import com.sest1601.bathingsites.Settings.SetingsFragment;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SetingsFragment())
                .commit();
    }
}
