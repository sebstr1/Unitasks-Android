package com.sest1601.lab7;

import com.sest1601.lab7.database.HistoryEntity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.ArrayMap;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DialPadCompound extends ConstraintLayout implements View.OnTouchListener, View.OnLongClickListener, View.OnClickListener {
    private SoundPool dialpadSounds;
    private boolean soundsLoaded;
    private EditText textfield;
    ArrayMap<Integer, Pair<String, Integer>> sounds;

    DialPadCompound(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {

        // Attach the XML to the activity (make it visible)
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflated = inflater.inflate(R.layout.dialpad_compound, this, true);

        textfield = inflated.findViewById(R.id.editText);
        textfield.setFocusable(false);
        textfield.setClickable(false);

        // Get all buttons from dialpad and set Listeners
        ArrayList<View> allButtons = inflated.getTouchables();
        for (View button : allButtons) {
            if (button.getId() == R.id.deleteButton) {
                button.setOnLongClickListener(this);
                button.setOnClickListener(this);
            } else if (button.getId() == R.id.callButton) {
                button.setOnClickListener(this);
            } else if (button.getId() == R.id.editText) {

            } else {
                button.setFocusable(true);
                button.setFocusableInTouchMode(true);
                button.setOnTouchListener(this);
            }
        }
    }

    public void loadExternalSounds() {
        // If storage is mounted
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //Set up audioattributes
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            // Build soundpool
            dialpadSounds = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
            String setting = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("audio_setting", null);


            // Load all 12 soundfiles & map to button ID
            sounds = new ArrayMap<>();
            sounds.put(R.id.imageButton1, Pair.create("1", dialpadSounds.load(setting + "one.mp3", 1)));
            sounds.put(R.id.imageButton2, Pair.create("2", dialpadSounds.load(setting + "two.mp3", 1)));
            sounds.put(R.id.imageButton3, Pair.create("3", dialpadSounds.load(setting + "three.mp3", 1)));
            sounds.put(R.id.imageButton4, Pair.create("4", dialpadSounds.load(setting + "four.mp3", 1)));
            sounds.put(R.id.imageButton5, Pair.create("5", dialpadSounds.load(setting + "five.mp3", 1)));
            sounds.put(R.id.imageButton6, Pair.create("6", dialpadSounds.load(setting + "six.mp3", 1)));
            sounds.put(R.id.imageButton7, Pair.create("7", dialpadSounds.load(setting + "seven.mp3", 1)));
            sounds.put(R.id.imageButton8, Pair.create("8", dialpadSounds.load(setting + "eight.mp3", 1)));
            sounds.put(R.id.imageButton9, Pair.create("9", dialpadSounds.load(setting + "nine.mp3", 1)));
            sounds.put(R.id.imageButton10, Pair.create("*", dialpadSounds.load(setting + "star.mp3", 1)));
            sounds.put(R.id.imageButton11, Pair.create("0", dialpadSounds.load(setting + "zero.mp3", 1)));
            sounds.put(R.id.imageButton12, Pair.create("#", dialpadSounds.load(setting + "pound.mp3", 1)));
            soundsLoaded = true;

        } else { // Storage not mounted
            Toast.makeText(getContext(), Environment.getExternalStorageState(),
                    Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onTouch(View buttonView, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonView.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorAccent));
            return true;
            // If releasing button - reset background tint and play sound
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            buttonView.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.defaultWhite));
            textfield.append(sounds.get(buttonView.getId()).first);
            if (soundsLoaded) {
                playSound(buttonView.getId());
            }
            return true;
        }
        return true;
    }

    // On long click - delete all text from the inputed numbers field.
    @Override
    public boolean onLongClick(View button) {
        if (button.getId() == R.id.deleteButton) {
            textfield.setText("");
        }
        return true;
    }

    @Override
    public void onClick(View button) {
        if (button.getId() == R.id.deleteButton) {
            String text = textfield.getText().toString();
            if (text.length() > 0) textfield.setText(text.substring(0, text.length() - 1));
        } else { // Call button was pressed
            initCall();
        }
    }

    // Check permission / Request permission / Call
    public void initCall() {
        if (!checkPermission()) {

            // If the user denied permission previously, Show an explanation why we need this
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(), Manifest.permission.CALL_PHONE)) {
                showExplanation();

            }
            // Request the permissions
            else {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.CALL_AND_GPS);

            }
        }
        // Permissions already given
        else {
            call();
        }
    }

    public void call() {

        // Check if number should be saved to history
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (sharedPref.getBoolean(getResources().getString(R.string.history_setting_key), true)) {

            // Create new thread to connect to db
            new Thread(new Runnable() {
                @Override
                public void run() {

                    HistoryEntity entity = new HistoryEntity();
                    Date today = Calendar.getInstance().getTime();
                    entity.setNumber(textfield.getText().toString());
                    entity.setDate(today.toString());
                    entity.setLng("No Permission");
                    entity.setLat("Not Permission");

                    // Save location if available
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                        if (locationManager != null) {
                            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            entity.setLat(String.valueOf(location.getLatitude()));
                            entity.setLng(String.valueOf(location.getLongitude()));
                        }
                    }

                    MainActivity.DB.historyDao().insert(entity);
                }
            }) .start();
    }




        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
           if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
               // Store location
               // db.storelocation(number)
           } else {
               Toast.makeText(getContext(), "No permission, cant save location!",
                       Toast.LENGTH_SHORT).show();
               //db.storedate(number)
           }

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + textfield.getText()));
            getContext().startActivity(intent);
        }else {
            Toast.makeText(getContext(), "No Permission, cant make calls!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermission() {
        int call = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);
        int gps = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        return call == PackageManager.PERMISSION_GRANTED &&
                gps == PackageManager.PERMISSION_GRANTED;
    }

    // Build a dialog that explains why permission is needed + listener that opens up permissionRequest again after user clicks it.
    public void showExplanation() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Call & Location");
        alertBuilder.setMessage("Grant both permissions to fully use this app!");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.CALL_AND_GPS);
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    // Plays sound according to what key is pressed
    private void playSound(int id) {
        dialpadSounds.play(sounds.get(id).second, 1.0f, 1.0f, 1, 0, 1.0f);
    }


    // ------------------------------- Physical Keyboard related --------------------------------------------------------------------------------------------------
    @Override
    public boolean onKeyDown(int key, KeyEvent event) {
        // Create fake motionEvent to send to onTouch
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        MotionEvent mE = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_DOWN,
                0.0f,
                0.0f,
                0
        );

        switch (key) {
            case KeyEvent.KEYCODE_1:
                onTouch(findViewById(R.id.imageButton1), mE);
                break;
            case KeyEvent.KEYCODE_2:
                onTouch(findViewById(R.id.imageButton2), mE);
                break;
            case KeyEvent.KEYCODE_3:
                if (event.isShiftPressed()) {
                    onTouch(findViewById(R.id.imageButton12), mE);
                } else {
                    onTouch(findViewById(R.id.imageButton3), mE);
                }
                break;
            case KeyEvent.KEYCODE_4:
                onTouch(findViewById(R.id.imageButton4), mE);
                break;
            case KeyEvent.KEYCODE_5:
                onTouch(findViewById(R.id.imageButton5), mE);
                break;
            case KeyEvent.KEYCODE_6:
                onTouch(findViewById(R.id.imageButton6), mE);
                break;
            case KeyEvent.KEYCODE_7:
                onTouch(findViewById(R.id.imageButton7), mE);
                break;
            case KeyEvent.KEYCODE_8:
                onTouch(findViewById(R.id.imageButton8), mE);
                break;
            case KeyEvent.KEYCODE_9:
                onTouch(findViewById(R.id.imageButton9), mE);
                break;
            case KeyEvent.KEYCODE_0:
                onTouch(findViewById(R.id.imageButton11), mE);
                break;
            case KeyEvent.KEYCODE_APOSTROPHE:
                if (event.isShiftPressed()) onTouch(findViewById(R.id.imageButton10), mE);
                break;
            case KeyEvent.KEYCODE_BACK:
                Activity host = (Activity) getContext();
                host.finish();
        }

        return true;


    }

    // Also Physical keyboard related
    @Override
    public boolean onKeyUp(int key, KeyEvent event) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        MotionEvent mE = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                0.0f,
                0.0f,
                0
        );

        switch (key) {
            case KeyEvent.KEYCODE_1:
                onTouch(findViewById(R.id.imageButton1), mE);
                break;
            case KeyEvent.KEYCODE_2:
                onTouch(findViewById(R.id.imageButton2), mE);
                break;
            case KeyEvent.KEYCODE_3:
                if (event.isShiftPressed()) {
                    onTouch(findViewById(R.id.imageButton12), mE);
                } else {
                    onTouch(findViewById(R.id.imageButton3), mE);
                }
                break;
            case KeyEvent.KEYCODE_4:
                onTouch(findViewById(R.id.imageButton4), mE);
                break;
            case KeyEvent.KEYCODE_5:
                onTouch(findViewById(R.id.imageButton5), mE);
                break;
            case KeyEvent.KEYCODE_6:
                onTouch(findViewById(R.id.imageButton6), mE);
                break;
            case KeyEvent.KEYCODE_7:
                onTouch(findViewById(R.id.imageButton7), mE);
                break;
            case KeyEvent.KEYCODE_8:
                onTouch(findViewById(R.id.imageButton8), mE);
                break;
            case KeyEvent.KEYCODE_9:
                onTouch(findViewById(R.id.imageButton9), mE);
                break;
            case KeyEvent.KEYCODE_0:
                onTouch(findViewById(R.id.imageButton11), mE);
                break;
            case KeyEvent.KEYCODE_STAR:
                onTouch(findViewById(R.id.imageButton10), mE);
                break;
            case KeyEvent.KEYCODE_APOSTROPHE:
                if (event.isShiftPressed()) onTouch(findViewById(R.id.imageButton10), mE);
                break;
        }
        return true;
    }
}
