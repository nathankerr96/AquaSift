package com.example.hjd.aquasift.Misc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.hjd.aquasift.Main.StartTest;
import com.example.hjd.aquasift.Misc.DisplayResultsTask;
import com.example.hjd.aquasift.Misc.UsbHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HJD on 12/28/2016.
 */

public class LinearSweepTask extends AsyncTask<Void, Void, Void>{


    private UsbHelper usbHelper;
    private Activity activity;
    private Context context;

    private ProgressDialog progressDialog;


    ArrayList<ArrayList<Integer>> dataList;

    public LinearSweepTask(UsbHelper usbHelper, Activity activity, Context context, ProgressDialog progressDialog) {
        this.usbHelper = usbHelper;
        this.activity = activity;
        this.context = context;
        this.progressDialog = progressDialog;



        int listSize;

        /*
        if(isCyclic == 1) {
            listSize = numCycles * 2;
        } else {
            //TODO Is this right?
            listSize = numCycles;
        }

        if (depositionEnabled == 1) {
            listSize += 1;
        }
        */

        dataList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Conducting Linear Sweep");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        usbHelper.startLinearSweep();

        byte shelvedValue = 0;
        boolean storedValue = false;

        boolean pickupNext = false;
        int currentList = 0;

        boolean depositionInProgress = false;

        ArrayList<Integer> workingList = new ArrayList<>();

        while (true) {
            byte[] data = {};
            try {
                Thread.sleep(100);
                data = usbHelper.read();
                Log.d("DEBUGGING", "Read Data: " + Arrays.toString(data));
            } catch (Exception InterruptedException) {
                //FJ
            }

            for (int i = 0; i < data.length; i += 2) {
                int value;
                if (storedValue) {
                    //TODO Does this ignore negatives?
                    value = ((shelvedValue & 0xFF) << 8) | (data[i] & 0xFF);
                    i -= 1;
                    storedValue = false;
                } else {
                    value = ((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF);
                }

                if (pickupNext) { //Next Sequence Number
                    currentList = value - 1;
                    pickupNext = false;
                    continue;
                }
                if (value == 0x8200) { //start sequence
                    pickupNext = true;
                    continue;
                }
                if (value == 0xFF00) {
                    if (!depositionInProgress) { //skip recording deposition
                        dataList.add(workingList);

                        Log.d("DEBUGGING", "SIZE: " + Integer.toString(workingList.size()));
                    } else {
                        depositionInProgress = false;
                    }
                    workingList = new ArrayList<>();
                    continue;
                        }
                if (value == 0x8000) { //start of deposition
                    depositionInProgress = true;
                    continue;
                }
                if (value == 0xFFF0) {
                    //end of test
                    return null;
                }

                workingList.add(value);

                if (i + 2 == data.length - 1) {
                    shelvedValue = data[i + 2];
                    storedValue = true;
                    i += 1;
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //super.onPostExecute(aVoid);
        DisplayResultsTask displayResultsTask = new DisplayResultsTask(usbHelper, activity,
                context, dataList, progressDialog);
        displayResultsTask.execute();
    }
}
