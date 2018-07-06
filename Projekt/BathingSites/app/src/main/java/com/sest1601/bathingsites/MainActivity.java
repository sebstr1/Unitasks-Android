package com.sest1601.bathingsites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.sest1601.bathingsites.Download.downloadActivity;
import com.sest1601.bathingsites.Settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check if default pref's been initialized! (in case user does not enter the settings activity, app wont work without this)
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(!(sp.getBoolean(getString(R.string.DEFAULT_PREFS_LOADED), false))) {
            PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean(getString(R.string.DEFAULT_PREFS_LOADED), true);
            edit.apply();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_CreateNewBathingSite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewBathingSiteActivity.class));
            }
        });
    }

    // Update count of bathsites incase it changed.
    @Override
    protected void onResume() {
        super.onResume();
        BathingSitesView k = findViewById(R.id.bathingSitesViewID);
        k.queryDB();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;

            case R.id.action_download:
                Intent d = new Intent(this, downloadActivity.class);
                startActivity(d);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
