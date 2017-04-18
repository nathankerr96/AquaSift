package com.example.hjd.aquasift.Misc;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by HJD on 4/10/2017.
 */

public class UploadDataTask extends AsyncTask<Void,Void,Void> {

    private String deviceId;

    private String date;
    private String latitude;
    private String longitude;
    private String testType;
    private String peakValues;
    private String concentration;

    public UploadDataTask(String[] data) {
        date = data[0];
        latitude = data[1];
        longitude = data[2];
        testType = data[3];
        peakValues = data[4];
        concentration = data[5];
    }

    @Override
    protected Void doInBackground(Void... params) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            Log.d("DEBUGGING", "HERE1");

            /*
            String postParameters = "date=" + s1 + "&data2=" + s2;
            byte[] postData = postParameters.getBytes("UTF-8");
            */

            /*
            String phpPost = null, phpPost2 = null;
            phpPost = URLEncoder.encode(s1, "UTF-8");
            phpPost2 = URLEncoder.encode(s2, "UTF-8");
            */

            //int postDataLength = postData.length;

            //TODO get device_id
            deviceId = "112";

            url = new URL("http://students.engr.scu.edu/~nkerr/aquasift/upload_data.php");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(   "date=" + date +
                            "&device_id=" + deviceId +
                            "&latitude=" + latitude +
                            "&longitude=" + longitude +
                            "&test_type=" + testType +
                            "&peak_values=" + peakValues +
                            "&concentration=" + concentration
            );

            writer.close();

            int x = urlConnection.getResponseCode();

            Log.d("DEBUGGING", "HERE2");

            /*
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            urlConnection.setUseCaches(false);

            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            out.write(postData);
            out.close();

            */

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
