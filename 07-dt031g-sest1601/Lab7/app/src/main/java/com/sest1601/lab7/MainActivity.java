package com.sest1601.lab7;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sest1601.lab7.database.HistoryDB;
import com.sest1601.lab7.download.downloadActivity;
import com.sest1601.lab7.history.HistoryActivity;
import com.sest1601.lab7.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity {


    public static final String TRANSLATE_JOB = "com.sest1601.URL";
    private DialPadCompound dialpad;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences prefs;
    public static HistoryDB DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the dialpad and set it up.
        dialpad = new DialPadCompound(this);
        dialpad.setFocusable(true);
        dialpad.setFocusableInTouchMode(true);
        setContentView(dialpad);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Setup a SharedPreference listener for changes
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("audio_setting")) {
                    dialpad.loadExternalSounds();
                }
            }


        };

        // Register the listener
        prefs.registerOnSharedPreferenceChangeListener(listener);

        // create database
        DB = Room.databaseBuilder(getApplicationContext(), HistoryDB.class, "HistoryDB")
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_history:
                Intent in = new Intent(this, HistoryActivity.class);
                startActivity(in);
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.download_activity:
                Intent download = new Intent(this, downloadActivity.class);
                download.putExtra(TRANSLATE_JOB, "http://dt031g.programvaruteknik.nu/dialpad/sounds/");
                download.putExtra("location", "fileLocation");
                startActivity(download);
                return true;
            case R.id.action_map:
                Intent map = new Intent(this, MapsActivity.class);
                startActivity(map);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Checks permissions
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.WRITE_EXT_STRORAGE);
        }
        // Permission already given
        else {
            dialpad.loadExternalSounds();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.WRITE_EXT_STRORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialpad.loadExternalSounds();
                } else {
                    Toast.makeText(this, "No Permission, no audio loaded!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case Constants.CALL_AND_GPS: {
                dialpad.call();
                break;
            }

        }

    }




    // Register the listener again on resume.
    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    // Remove the listener on pause.
    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
