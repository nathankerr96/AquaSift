package com.example.hjd.aquasift.Misc;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HJD on 4/10/2017.
 */

public class NetworkTestTask extends AsyncTask<Void,Void,Void> {

    @Override
    protected Void doInBackground(Void... params) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://students.engr.scu.edu/~nkerr/aquasift/test.php");
            urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String response;
            while ((response = in.readLine()) != null) {
                Log.d("DEBUGGING", response);
            }
            in.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("DEBUGGING", "MalformedURLException!");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("DEBUGGING", "IOException!");
        } finally {
            urlConnection.disconnect();
        }

        return null;
    }
}
