package com.sest1601.bathingsites.Download;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;
import com.sest1601.bathingsites.database.BathsiteDB;
import com.sest1601.bathingsites.database.BathsiteEntity;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

// Class to handle Async downloading & unzipping
public class AsyncDownload extends AsyncTask<URL, Integer, Integer> {

    private static WeakReference<View> pView;
    private static String fileName;

    // Constructor that sets up WeakReferences
    AsyncDownload(View v) {
        pView = new WeakReference<>(v);
    }

    // Just before executing set up
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pView.get().setVisibility(View.VISIBLE);
    }

    // Background thread - Download and unzip
    protected Integer doInBackground(URL... urls) {
        try {

            // Create URL & HTTP connection
            URL url = urls[0];
            String filePath = downloadFile(url);

            // Open, parse and add data to DB
            parseFile(filePath);

            // Delete the file.
            File file = new File(filePath);
            file.delete();
            return null;

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    private void parseFile(String filePath) {
        String line = "";
        String tmp = "";
        String lng = "";
        String lat = "";
        String address = "";
        String name = "";
        BufferedReader br = null;
        BathsiteDB db = BathsiteDB.getInstance(pView.get().getContext());
            try {
                br = new BufferedReader(new FileReader(filePath));
            } catch (IOException e) {
                Log.e("IOException", "While trying to create FileReader from filepath");
            }

            try {
                if (br != null) {
                    // Read line by line
                    while ((line = br.readLine()) != null) {
                        address = "";

                        // filter out the coordinates
                        tmp = line.substring(1);
                        String[] splitted = tmp.split(",", 10);

                        for (int i = 0; i < splitted.length; i++) {
                            if (i == 0) {
                                lng = splitted[i].replaceAll("\"", "");
                            } else if (i == 1) {
                                lat = splitted[i].replaceAll("\"", "");
                            } else if (i == 2) {
                                name = splitted[i].replaceAll("\"", "");
                            } else {
                                address = address + " " + splitted[i].replaceAll("\"", "");
                            }
                        }
                        // Check if we have entity in db already
                        int exists = db.dao().siteExists(lng, lat);

                        if (!(exists > 0)) {
                            BathsiteEntity entity = new BathsiteEntity();
                            entity.setName(name);
                            entity.setAddress(address);
                            entity.setLng(lng);
                            entity.setLat(lat);
                            try {
                                db.dao().insert(entity);
                            } catch (SQLiteConstraintException e) {
                                Log.e("SQLITE", "Lat long already exists");
                            }
                        } else {
                            Log.e("SQLITE", "Lng Lat combo already exists");
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("IOexception", "IOexception while trying to read from csv file.");

            }

    }

    private String downloadFile(URL url) {
        int count;
        String filePath = "";
        try {
            File folder = new File(pView.get().getContext().getExternalFilesDir(null) + "/downloads");

            if (!folder.exists()) {
                folder.mkdir();
            }
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            conection.setRequestMethod("GET");
            conection.connect();

            fileName = URLUtil.guessFileName(url.toString(), null, null);

            filePath =  folder.getAbsolutePath() + "/" + fileName;
            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream
            OutputStream output = new FileOutputStream(folder.getAbsolutePath() + "/" + fileName);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                if (!isCancelled()) {
                    total += count;
                    // writing data to file
                    output.write(data, 0, count);
                }
            }
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();
            conection.disconnect();

        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "File not found when downloading csv");
        } catch (ProtocolException e) {
            Log.e("ProtocolException", "Protocol error when downloading csv");
        } catch (IOException e) {
            Log.e("IOException", "IOException when downloading csv");
        }

        return filePath;
    }

    // If download canceled
    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(pView.get().getContext(), fileName + " Interrupted",
                Toast.LENGTH_SHORT).show();
    }
    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(Integer result) {
        pView.get().setVisibility(View.INVISIBLE);
        Toast.makeText(pView.get().getContext(), fileName + " Downloaded!",
                Toast.LENGTH_LONG).show();

    }
}