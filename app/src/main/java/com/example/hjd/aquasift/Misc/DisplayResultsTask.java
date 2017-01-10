package com.example.hjd.aquasift.Misc;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;

import com.example.hjd.aquasift.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;

/**
 * Created by HJD on 12/30/2016.
 */

public class DisplayResultsTask extends AsyncTask<Void, DataPoint, Void> {

    UsbHelper usbHelper;
    Activity activity;
    Context context;
    ArrayList<ArrayList<Integer>> dataList;

    private int reverse;

    private int startVoltage;
    private int endVoltage;

    private int gainResistor;

    private ArrayList<ArrayList<Pair<Float, Float>>> currentList;
    private ArrayList<ArrayList<Pair<Float, Float>>> smoothedCurrentList;

    private GraphView graph;

    public DisplayResultsTask(UsbHelper usbHelper, Activity activity,
                              Context context, ArrayList<ArrayList<Integer>> dataList) {
        this.usbHelper = usbHelper;
        this.activity = activity;
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    protected void onPreExecute() {
        startVoltage = usbHelper.getSweepStartVoltage();
        endVoltage = usbHelper.getSweepEndVoltage();

        gainResistor = usbHelper.getGainResistor();

        graph = (GraphView) activity.findViewById(R.id.graph);

        currentList = new ArrayList<>();
        smoothedCurrentList = new ArrayList<>();

        if (startVoltage > endVoltage) {
            reverse = 1;
        } else {
            reverse = 0;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        float currentVoltage;
        int voltageDiff = abs(startVoltage-endVoltage);
        float voltageIncrement;


        for (int i=0; i < dataList.size(); i++) {
            ArrayList<Integer> activeDataList = dataList.get(i);
            ArrayList<Pair<Float,Float>> activeCurrentList = new ArrayList<>();
            ArrayList<Pair<Float, Float>> activeSmoothedCurrentList = new ArrayList<>();

            currentVoltage = startVoltage;

            voltageIncrement = (float)voltageDiff / activeDataList.size();
            //int numDataPoints = (int) (voltageDiff / voltageIncrement);
            //graph every nth point based on divisor = Total points to graph
            int criticalPoints = (int) Math.ceil((float)activeDataList.size()/1000);
            if (criticalPoints < 1) {
                criticalPoints = 1;
            }

            Log.d("DEBUGGING", "Critical: " + Integer.toString(criticalPoints));

            int start;
            int stop;
            int delta;
            if (i+reverse % 2 == 0) { //low to high
                start = 0;
                stop = activeDataList.size();
                delta = 1;
            } else {
                start = activeDataList.size() - 1;
                stop = -1;
                delta = -1;
            }

            ArrayList<DataPoint> dataPointsToGraph = new ArrayList<>();
            float movingAverage = 0;
            int numPointsInAverage = 0;
            int windowWidth = 3;

            for (int j = start ; j != stop; j += delta) {
                float current = (float)((activeDataList.get(j)) * (3.3/4096) / gainResistor * 1000000);
                activeCurrentList.add(new Pair<>(currentVoltage, current));

                if (numPointsInAverage < windowWidth) {
                    numPointsInAverage += 1;
                    movingAverage = movingAverage + ((1f / numPointsInAverage) * current);
                    activeSmoothedCurrentList.add(new Pair<Float, Float>(currentVoltage, movingAverage));
                } else {
                    if (start == 0) {
                        movingAverage = movingAverage + ((1f / windowWidth) * current) -
                                (activeCurrentList.get(j - windowWidth).second * ((1f / windowWidth)));
                    } else {
                        movingAverage = movingAverage + ((1f / windowWidth) * current) -
                                (activeCurrentList.get(start-j-windowWidth).second * ((1f / windowWidth)));
                    }
                    activeSmoothedCurrentList.add(new Pair<Float, Float>(currentVoltage, movingAverage));
                }

                if (j % criticalPoints == 0) {
                    dataPointsToGraph.add(new DataPoint(currentVoltage, current));
                }

                currentVoltage += voltageIncrement;
            }

            currentList.add(activeCurrentList);
            smoothedCurrentList.add(activeSmoothedCurrentList);


            DataPoint[] toPublish = dataPointsToGraph.toArray(new DataPoint[dataPointsToGraph.size()]);
            publishProgress(toPublish);
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(DataPoint... dataPoints) {
        LineGraphSeries<DataPoint> lineGraphSeriesToGraph = new LineGraphSeries<>(dataPoints);
        Log.d("DEBUGGING", "Graphing Data!");
        graph.addSeries(lineGraphSeriesToGraph);
    }
}
