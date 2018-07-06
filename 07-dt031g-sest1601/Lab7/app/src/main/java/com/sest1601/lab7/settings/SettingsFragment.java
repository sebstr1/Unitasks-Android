package com.sest1601.lab7.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import com.sest1601.lab7.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends PreferenceFragment { //implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mSharedPreferences;
    private PreferenceCategory mMyPreferenceCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);


        // Create the new ListPref
        ListPreference listPref = new ListPreference(getContext());

        // Get the Preference Category which we want to add the ListPreference to
        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("audio_category");

        File folder;

        try {
            folder = new File(getContext().getExternalFilesDir(null) + "/sounds");
            if (!folder.exists()) {
                folder.mkdir();
            }

            String path = folder.getAbsolutePath();
            File f = new File(path);
            File[] files = f.listFiles();

            List<String> entries = new ArrayList<>();
            List<String> values = new ArrayList<>();

            for (File file : files) {
                if (file.isDirectory()) {
                    entries.add(file.getName());
                    values.add(file.getAbsolutePath() + "/");
                }
            }

            final CharSequence[] entrySequence = entries.toArray(new CharSequence[entries.size()]);
            final CharSequence[] valueSequence = values.toArray(new CharSequence[values.size()]);


            // Setting the entries & values
            listPref.setEntries(entrySequence);
            listPref.setEntryValues(valueSequence);

            listPref.setKey("audio_setting");
            listPref.setTitle("Select voice");
            listPref.setSummary("choose from downloaded audio");
            listPref.setDialogTitle("Select voice");
            listPref.setPersistent(true);

            // Add the ListPref to the Pref category
            targetCategory.addPreference(listPref);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}