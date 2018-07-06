package com.sest1601.bathingsites.new_site;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.sest1601.bathingsites.R;
import com.sest1601.bathingsites.settings.SettingsActivity;
import com.sest1601.bathingsites.weather.AsyncGetWeather;
import com.sest1601.bathingsites.database.AsyncDbSave;

import java.util.ArrayList;

public class NewBathingSiteActivity extends AppCompatActivity {
    private View pView;
    private ArrayList<EditText> textFields;
    private RatingBar ratingBar;
    private EditText name, desc, addr, lng, lat, temp, temp_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bathing_site);
        Toolbar toolbar = findViewById(R.id.newBath_toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    // Clear all inputfields
    private void clearAll() {
        for (EditText field : textFields) {
            field.setText("");
            field.setError(null);

        }
        ratingBar.setRating(0);
    }

    // Check that all required fields have input
    private boolean checkRequiredFields() {
        boolean succcess = true;

        if (TextUtils.isEmpty(name.getText())) {
            name.setError("Name is required!");
            succcess = false;
        }

        if (TextUtils.isEmpty(addr.getText()) && TextUtils.isEmpty(lat.getText()) && TextUtils.isEmpty(lng.getText())) {
            addr.setError("Address is required!");
            lng.setError("Longitude is required!");
            lat.setError("Latitude is required!");
            succcess = false;
        } else if (TextUtils.isEmpty(addr.getText())) {

            if (TextUtils.isEmpty(lng.getText())) {
                lng.setError("Longitude is required!");
                succcess = false;
            }
            if (TextUtils.isEmpty((lat.getText()))) {
                lat.setError("Latitude is required!");
                succcess = false;
            }
        }
        return succcess;
    }


    // Save site to db
    private void saveNewBathSite() {
        new AsyncDbSave(textFields, ratingBar, new AsyncDbSave.OnFetchFinishedListener() {
            @Override
            public void onFetchFinished(Boolean result) {
                // Result from the AsyncTask
                dbSaveResult(result);
            }
        }).execute();

    }


    private void dbSaveResult(boolean result) {
        // Db save success
        if (result) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Save successfull");
            alertBuilder.setMessage("Bathingsite saved to DB!");
            alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clearAll();
                    dialog.dismiss();
                    finish();

                }
            });

            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
        // Db save failed
        else {
            createDialogMessage("Save failed", "Bathsite with this coordinates already" +
                    "exists, cant save duplicate locations");
        }
    }

    // Check if field is empty
    private boolean notEmpty(EditText field) {
        if (TextUtils.isEmpty(field.getText())) {
            return false;
        } else {
            return true;
        }
    }

    private void init() {

        setTitle(getString(R.string.newSiteTitle));
        pView = findViewById(R.id.view);
        pView.setVisibility(View.INVISIBLE);

        // Add ratingbar
        ratingBar = findViewById(R.id.newBath_Ratingbar);
        name  = findViewById(R.id.newBath_Field_Name);
        desc  = findViewById(R.id.newBath_Field_Desc);
        addr  = findViewById(R.id.newBath_Field_Address);
        lng  = findViewById(R.id.newBath_Field_Longitude);
        lat  = findViewById(R.id.newBath_Field_Latitude);
        temp  = findViewById(R.id.newBath_Field_WaterTEMP);
        temp_date  = findViewById(R.id.newBath_Field_WaterTEMP_DATE);

        textFields = new ArrayList<EditText>() {{
            add(name);
            add(desc);
            add(addr);
            add(lng);
            add(lat);
            add(temp);
            add(temp_date);
        }};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bathing_site, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.newBath_menu_CLEAR:
                clearAll();
                return true;
            case R.id.newBath_menu_save:
                if (checkRequiredFields()) saveNewBathSite();
                return true;
            case R.id.newBath_menu_weather:
                getWeather();
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Checks if we have what is required to fetch weather from location
    private void getWeather() {

        boolean requirements = false;
        String locationparam = "";
        // We have an address
        if (notEmpty(addr)) {
            requirements = true;
//            https://stackoverflow.com/questions/16974267/how-to-remove-only-trailing-spaces-of-a-string-in-java-and-keep-leading-spaces/16974310
            String s = addr.getText().toString().replaceFirst("\\s++$", "");
            locationparam = s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        // We have both coordinates
        if (notEmpty(lat) && notEmpty(lng)) {
            requirements = true;
            locationparam = lng.getText().toString() + "|" + lat.getText().toString();
        }



        // If requirements met to fetch weather
        if (requirements) {
            new AsyncGetWeather(pView, locationparam, new AsyncGetWeather.OnFetchFinishedListener() {
                @Override
                public void onFetchFinished(Pair<String, Drawable> result) {
                    // Result from the AsyncTask
                    printResult(result);
                }
            }).execute();
        }
        // We did not have what was required, make an dialog to tell the user to fix it.
        else {
            createDialogMessage("Cant get weather", "Enter addres or both coordinates to get weather!");
        }

    }

    private void printResult(Pair<String, Drawable> r) {

        if (!r.first.equals("")) {

            // Find temp
            String tmp = r.first.substring(r.first.indexOf("temp_c:") + 7);
            String temp = tmp.substring(0, tmp.indexOf("<"));

            // Find condition
            tmp = r.first.substring(r.first.indexOf("condition") + 10);
            String condition = tmp.substring(0, tmp.indexOf("<"));


            if (temp.equals("null") && condition.equals("null")) {
                // Was some kind of problem loading data from users input, tell the user.
                createDialogMessage("Problem loading weatherdata", "Could not load data from your input, please correct address or coordinate fields");
            } else {

                // Success loading data, show the weather to the user!
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Current Weather");
                alertBuilder.setMessage(temp + "℃" + "\n" + condition);
                alertBuilder.setIcon(r.second);
                alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        } else {
            createDialogMessage("Invalid weathersource", "You have to go to settings and set your source to a valid one.");
        }

    }

    // Create dialog with title and message
    private void createDialogMessage(String title, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }


}
