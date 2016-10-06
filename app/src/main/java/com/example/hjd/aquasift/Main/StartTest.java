package com.example.hjd.aquasift.Main;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Process;
import android.provider.ContactsContract;
import android.support.annotation.FloatRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hjd.aquasift.Misc.DbHelper;
import com.example.hjd.aquasift.Misc.FTDriver;
import com.example.hjd.aquasift.Misc.UsbHelper;
import com.example.hjd.aquasift.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StartTest extends AppCompatActivity {

    int[] raw_data;


    Button save_data_button;
    GraphView graph;

    List<LineGraphSeries<DataPoint>> lineGraphSeriesArrayList;




    UsbManager manager;

    UsbHelper usbHelper;

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        Bundle b = getIntent().getExtras();
        String[] commands = b.getStringArray(MainActivity.COMMANDS_EXTRA);

        String command1 = commands[0];
        String command2 = commands[1];
        String command3 = commands[2];
        String command4 = commands[3];



        manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        usbHelper = new UsbHelper(manager);
        int ret = usbHelper.begin();
        Log.d("DEBUGGING", Integer.toString(ret));

        usbHelper.setNumElectrodes(2);
        usbHelper.setDataRate(50);
        usbHelper.enableDeposition(0);
        usbHelper.setCyclic(1);
        usbHelper.setNumCycles(2);
        usbHelper.getSettings();


        graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-1000);
        graph.getViewport().setMaxX(1000);

        lineGraphSeriesArrayList = new ArrayList<>(usbHelper.getNumCycles());
        for (int i=0; i<usbHelper.getNumCycles(); i++) {
            lineGraphSeriesArrayList.add(new LineGraphSeries<DataPoint>());
            graph.addSeries(lineGraphSeriesArrayList.get(i));
        }


        RunTest runTest = new RunTest();
        runTest.execute();

        save_data_button = (Button) findViewById(R.id.save_data_button);
        save_data_button.setVisibility(View.INVISIBLE);


        save_data_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread save_data_thread = new Thread(new SaveData());
                save_data_thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                save_data_thread.run();

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }

    private class RunTest extends AsyncTask<String, List<DataPoint>, LineGraphSeries> {

        int currentSweepNum;
        boolean nextSweep;


        protected void onPreExecute() {
            currentSweepNum = 1;

            if(usbHelper.getDepositionTime() != 0) {
                //TODO Add Progress bar for deposition period
            }

            usbHelper.startLinearSweep();
        }

        protected LineGraphSeries<DataPoint> doInBackground(String... params) {

            float voltageIncrement = usbHelper.getSweepVoltageIncrement();
            int startVoltage = usbHelper.getSweepStartVoltage();
            float currentVoltage = startVoltage;

            while(true) {
                try {
                    Thread.sleep(100);
                    nextSweep = false;
                    byte[] data = usbHelper.read();
                    Log.d("DEBUGGING", Arrays.toString(data));
                    List<DataPoint> newDataPoints = new ArrayList<>();
                    List<DataPoint> prevNewDataPoints = null;
                    for(int i=0; i<data.length; i+=2) {
                        int value = ((data[i]&0xFF)<<8) | (data[i+1]&0xFF);
                        if (value == 0x8200) {
                            currentSweepNum = ((data[i+2]&0xFF) | (data[i+3]&0xFF)); //TODO What if array ends in 0x8200?
                            nextSweep = true;
                            prevNewDataPoints = newDataPoints;
                            newDataPoints.clear();
                            i += 2;
                            continue;
                        }
                        if (value == 0xFF00 || value == 0x8000) {
                            continue;
                        }
                        if (value == 0xFFF0) {
                            return null;
                        }
                        newDataPoints.add(new DataPoint(currentVoltage, value));
                        currentVoltage += voltageIncrement;
                        Log.d("DEBUGGING", "VOLTAGE: "  + Float.toString(currentVoltage));
                    }

                    publishProgress(newDataPoints, prevNewDataPoints);

                } catch (Exception e) {
                    //TODO handle exception
                    e.printStackTrace();


                }
            }
        }


        @Override
        protected void onProgressUpdate(List<DataPoint>... L) {
            int nextSeries = 0;
            if (nextSweep && currentSweepNum != 1) {
                for (DataPoint dataPoint : L[0]) {
                    lineGraphSeriesArrayList.get(currentSweepNum-2).appendData(dataPoint, false, 2000000);
                }
                nextSeries = 1;
            }

            for (DataPoint dataPoint : L[nextSeries]) {
                Log.d("DEBUGGING", "Sweep Num: " + Integer.toString(currentSweepNum));
                Log.d("DEBUGGING", "NUM: " + Integer.toString(lineGraphSeriesArrayList.size()));
                lineGraphSeriesArrayList.get(currentSweepNum-1).appendData(dataPoint, false, 2000000);
                //s.appendData(dataPoint, false, 2000000);
            }


        }

        @Override
        protected void onPostExecute(LineGraphSeries s) {
            Toast.makeText(getBaseContext(), "Finished AsyncTask!", Toast.LENGTH_SHORT).show();
            save_data_button.setVisibility(View.VISIBLE);

            //graph.addSeries(s);
        }
    }

    private class SaveData implements Runnable {

        public void run() {
            DbHelper dbHelper = new DbHelper(getBaseContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String raw_data_string = Arrays.toString(raw_data);

            ContentValues values = new ContentValues();
            values.put(DbHelper.COL_USER_ID, "343");
            values.put(DbHelper.COL_DATE, "12/23/16");
            values.put(DbHelper.COL_RAW_DATA, raw_data_string);
            values.put(DbHelper.COL_TEST_TYPE, "Arsenic");

            db.insertOrThrow(DbHelper.TABLE_NAME, null, values);

            Log.d("DEBUGGING", raw_data_string);
            Log.d("DEBUGGING", "THREAD FINISHED");
        }

    }
}


