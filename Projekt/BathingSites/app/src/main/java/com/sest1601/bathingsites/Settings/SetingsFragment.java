package com.sest1601.bathingsites.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.sest1601.bathingsites.R;

public class SetingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Find the weathersource pref and set its summary to pref value
        Preference pref = findPreference(getString(R.string.pref_key_weatherSource));
        EditTextPreference prefText = (EditTextPreference) pref;
        pref.setSummary(prefText.getText());

        // Find the downloadsource pref and set its summary to pref value
        pref = findPreference(getString(R.string.pref_key_downloadSource));
        prefText = (EditTextPreference) pref;
        pref.setSummary(prefText.getText());


        // Adds a way to reset preferences to default setting
        Preference reset = findPreference(getResources().getString(R.string.pref_resetOnClick));
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Preference weatherSource = findPreference(getString(R.string.pref_key_weatherSource));
                ((EditTextPreference) weatherSource).setText(getString(R.string.pref_weatherSourceDEFUALT));

                Preference downloadSource = findPreference(getString(R.string.pref_key_downloadSource));
                ((EditTextPreference) downloadSource).setText(getString(R.string.pref_key_downloadSourceDEFUALT));

                return true;
            }
        });

    }

    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Find the pref and set the summary to the pref's value.
        if (key.equals(getString(R.string.pref_key_weatherSource))) {
            Preference pref = findPreference(getString(R.string.pref_key_weatherSource));
            EditTextPreference prefText = (EditTextPreference) pref;
            pref.setSummary(prefText.getText());
        }

        if (key.equals(getString(R.string.pref_key_downloadSource))) {
            Preference pref = findPreference(getString(R.string.pref_key_downloadSource));
            EditTextPreference prefText = (EditTextPreference) pref;
            pref.setSummary(prefText.getText());
        }

    }
}