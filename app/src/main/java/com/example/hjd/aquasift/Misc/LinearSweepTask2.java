package com.example.hjd.aquasift.Misc;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.hjd.aquasift.Main.StartTest;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HJD on 12/16/2016.
 */

public class LinearSweepTask2 extends AsyncTask<String, Integer, String>{

    private Context context;

    private ProgressDialog progressDialog;
    private int maxProgress;

    private int numCycles;
    private int isCyclic;
    private int depositionEnabled;
    private int gainResistor;
    private int startVoltage;
    private int endVoltage;
    private float voltageIncrement;

    private GraphView graph;

    private UsbHelper usbHelper;

    List<List<Integer>> dataList;

    public LinearSweepTask2(Context passedContext, UsbHelper passedHelper, GraphView passedGraphView) {

        context = passedContext;
        usbHelper = passedHelper;
        graph = passedGraphView;

        isCyclic = usbHelper.isCyclic();
        numCycles = usbHelper.getNumCycles();
        depositionEnabled = usbHelper.getDepositionStatus();
        gainResistor = usbHelper.getGainResistor();
        startVoltage = usbHelper.getSweepStartVoltage();
        endVoltage = usbHelper.getSweepEndVoltage();
        voltageIncrement = usbHelper.getSweepVoltageIncrement();

        int listSize;

        if(isCyclic == 1) {
            listSize = numCycles * 2;
        } else {
            //TODO Is this right?
            listSize = numCycles;
        }

        if (depositionEnabled == 1) {
            listSize += 1;
        }

        dataList = new ArrayList<>();

    }

    @Override
    protected void onPreExecute() {


        //have a progress element for each cycles + deposition period
        if (numCycles == 0) {
            maxProgress = 1;
        } else {
            maxProgress = 2 * numCycles;
        }
        maxProgress += depositionEnabled;

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxProgress);
        progressDialog.setProgress(1);

        if (depositionEnabled == 1) {
            progressDialog.setMessage("Deposition Period");
        } else {
            progressDialog.setMessage("Sweep 1/" + Integer.toString(maxProgress));
        }

        //TODO Maybe check to make sure not already showing?
        progressDialog.show();

        usbHelper.startLinearSweep();
    }

    @Override
    protected void onPostExecute(String s) {

        for (int i=0; i < dataList.size(); i++) {
            List<Integer> dataGraph = dataList.get(i);
            Log.d("DEBUGGING", "Size of list " + i + ":" + dataGraph.size());
            LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>();


        }

        progressDialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int nextList = values[0]+1;



        progressDialog.setProgress(nextList);
        progressDialog.setMessage("Sweep " + Integer.toString(nextList) + "/" +
                Integer.toString(maxProgress));


        /*
        int currentVoltage;
        if (startVoltage > endVoltage) {
            if (toUpdate % 2 == 1) {
                currentVoltage = endVoltage;
                for (int i = 0; i < toGraph.size(); i++) {
                    int current = (int)((toGraph.get(i)-2047) * (3.3/4096) / gainResistor * 1000000);
                    DataPoint dp = new DataPoint(currentVoltage, current);
                    lineGraphSeries.appendData(dp, false, 200000);
                    currentVoltage += voltageIncrement;
                    Log.d("DEBUGGING", "Graphing: " + Integer.toString(toGraph.get(i)) +
                            "  Voltage: " + Float.toString(currentVoltage));
                }
            } else {
                currentVoltage = endVoltage;
                for (int i=toGraph.size()-1; i >= 0; i--) {
                    int current = (int)((toGraph.get(i)-2047) * (3.3/4096) / gainResistor * 1000000);
                    DataPoint dp = new DataPoint(currentVoltage, current);
                    lineGraphSeries.appendData(dp, false, 200000);
                    currentVoltage += voltageIncrement;
                }
            }
        } else {
            if (toUpdate % 2 == 1) {
                currentVoltage = startVoltage;
                for (int i = 0; i < toGraph.size(); i++) {
                    int current = (int)((toGraph.get(i)-2047) * (3.3/4096) / gainResistor * 1000000);
                    DataPoint dp = new DataPoint(currentVoltage, current);
                    lineGraphSeries.appendData(dp, false, 200000);
                    currentVoltage += voltageIncrement;
                }
            } else {
                currentVoltage = startVoltage;
                for (int i=toGraph.size()-1; i >= 0; i--) {
                    int current = (int)((toGraph.get(i)-2047) * (3.3/4096) / gainResistor * 1000000);
                    DataPoint dp = new DataPoint(currentVoltage, current);
                    lineGraphSeries.appendData(dp, false, 200000);
                    currentVoltage += voltageIncrement;
                }
            }
        }


        graph.addSeries(lineGraphSeries);
        */
    }

    @Override
    protected String doInBackground(String... params) {


        byte shelvedValue = 0;
        boolean storedValue = false;

        boolean pickupNext = false;
        int currentList = 0;

        boolean depositionInProgress = false;

        List<Integer> workingList = new ArrayList<>();

        while (true) {
            byte[] data = {};
            try {
                Thread.sleep(100);
                data = usbHelper.read();
                Log.d("DEBUGGING", Arrays.toString(data));
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < data.length - 1; i += 2) {
                int value;

                if (storedValue) { //all data is 2 bytes so handle case when second byte was cut off
                    value = ((shelvedValue & 0xFF) << 8) | (data[i] & 0xFF);
                    i -= 1;
                    storedValue = false;
                } else {
                    value = ((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF);
                }

                if (pickupNext) { //next sequence number
                    currentList = value - 1;
                    pickupNext = false;
                    continue;
                }
                if (value == 0x8200) { //next sequence indicator
                    pickupNext = true;

                    continue;
                }
                if (value == 0xFF00) { //End of Block
                    if (!depositionInProgress) {
                        dataList.add(workingList);
                        Log.d("DEBUGGING", "SIZE: " + Integer.toString(workingList.size()));
                        publishProgress(currentList);

                    }
                    workingList = new ArrayList<>();

                    depositionInProgress = false;
                    continue;
                }
                if (value == 0x8000) {
                    //start of deposition
                    depositionInProgress = true;
                    continue;
                }
                if (value == 0xFFF0) {
                    publishProgress(currentList);

                    return null; //end
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
}

