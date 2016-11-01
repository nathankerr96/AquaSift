package com.example.hjd.aquasift.Main;

import android.app.PendingIntent;
import android.app.ProgressDialog;
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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StartTest extends AppCompatActivity {

    Button save_data_button;
    GraphView graph;

    List<List<Integer>> dataList;
    List<LineGraphSeries<DataPoint>> lineGraphSeriesList;

    UsbManager manager;
    UsbHelper usbHelper;

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
        usbHelper.setDataRate(10);
        usbHelper.setCyclic(1);
        usbHelper.setNumCycles(1);
        usbHelper.getSettings();


        graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-1000);
        graph.getViewport().setMaxX(1000);

        int numCycles = usbHelper.getNumCycles();

        dataList = new ArrayList<>();
        lineGraphSeriesList = new ArrayList<>(numCycles);

        for (int i=0; i < numCycles; i++) {
            dataList.add(new ArrayList<Integer>());
            lineGraphSeriesList.add(new LineGraphSeries<DataPoint>());
            graph.addSeries(lineGraphSeriesList.get(i));
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

    private class RunTest extends AsyncTask<String, Number, LineGraphSeries> {

        int numCycles;

        boolean nextSweep;

        int currentList;

        boolean pickupNext = false;

        List<DataPoint> graphBuffer = new ArrayList<>();

        int lastAddedToPlot;

        ProgressDialog pdialog;


        protected void onPreExecute() {
            currentList = 0;
            numCycles = usbHelper.getNumCycles();

            usbHelper.startLinearSweep();

            pdialog = new ProgressDialog(getBaseContext(), ProgressDialog.STYLE_HORIZONTAL);
            pdialog.setTitle("Running Test");
            pdialog.setCancelable(false);
            if(usbHelper.getDepositionTime() != 0) {
                //TODO Add Progress bar for deposition period
            }

            pdialog.setMessage("Sweep 1/" + numCycles);
            if(!pdialog.isShowing()) {
                pdialog.show();
            }

        }

        protected LineGraphSeries<DataPoint> doInBackground(String... params) {

            float voltageIncrement = usbHelper.getSweepVoltageIncrement();
            int startVoltage = usbHelper.getSweepStartVoltage();
            float currentVoltage = startVoltage;

            byte shelvedValue = -1;
            boolean storedValue = false;

            int lastValue = 1500;

            while(true) {

                byte[] data = {};

                try {
                    Thread.sleep(100);
                    nextSweep = false;
                    data = usbHelper.read();
                    Log.d("DEBUGGING", Arrays.toString(data));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for(int i=0; i<data.length-1; i+=2) {
                    int value;

                    if (storedValue) {
                        value = ((shelvedValue&0xFF)<<8) | (data[i]&0xFF);
                        i -= 1;
                        shelvedValue = -1;
                        storedValue = false;
                    } else {
                        value = ((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF);
                    }

                    if (pickupNext) { //next sequence number
                        currentList = value - 1;
                        pickupNext = false;
                        voltageIncrement = -voltageIncrement;
                        continue;
                    }
                    if (value == 0x8200) { //next sequence indicator
                        pickupNext = true;
                        continue;
                    }
                    if (value == 0xFF00 || value == 0x8000) {
                        continue;
                    }
                    if (value == 0xFFF0) {
                        return null; //end
                    }

                    dataList.get(currentList).add(value);
                    currentVoltage += voltageIncrement;

                    if (value-lastValue > 100 || value-lastValue < -100) {
                        continue; //TODO skips blips, NOT a solution
                    }
                    lastValue = value;


                    if (value - lastAddedToPlot > 4 || value - lastAddedToPlot < -4) {
                        publishProgress(value, currentVoltage, currentList);
                    }

                    if (i+2 == data.length-1) {
                        shelvedValue = data[i+2];
                        storedValue = true;
                        i += 1;
                    }

                    //Log.d("DEBUGGING", "VOLTAGE: "  + Float.toString(currentVoltage));
                }
            }
        }

        @Override
        protected void onProgressUpdate(Number... passedValue) {
            /*
            int value = passedValue[0].intValue();
            float voltage = passedValue[1].floatValue();
            int seriesNum = passedValue[2].intValue();


            DataPoint dp = new DataPoint(voltage, value);
            lineGraphSeriesList.get(seriesNum).appendData(dp, false, 100000);
            */

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

            //TODO FIX THIS!!
            String raw_data_string = "Temp";

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


