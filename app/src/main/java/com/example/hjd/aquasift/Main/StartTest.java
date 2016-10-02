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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class StartTest extends AppCompatActivity {

    int[] raw_data;


    Button save_data_button;
    GraphView graph;

    LineGraphSeries<DataPoint> s = new LineGraphSeries<>();


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

        graph = (GraphView) findViewById(R.id.graph);

        graph.addSeries(s);


        manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        usbHelper = new UsbHelper(manager);
        int ret = usbHelper.begin();
        Log.d("DEBUGGING", Integer.toString(ret));

        usbHelper.setNumElectrodes(2);
        usbHelper.setDataRate(50);
        usbHelper.getSettings();
        

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

    private class RunTest extends AsyncTask<String, DataPoint, LineGraphSeries> {




        protected void onPreExecute() {
            usbHelper.startLinearSweep();
        }

        protected LineGraphSeries<DataPoint> doInBackground(String... params) {

            int count = 0;

            while(true) {
                try {
                    Thread.sleep(100);
                    byte[] data = usbHelper.read();
                    Log.d("DEBUGGING", Arrays.toString(data));
                    DataPoint[] dataToAdd = new DataPoint[data.length/2];
                    for(int i=0; i<data.length; i+=2) {
                        int value = ((data[i]&0xFF)<<8) | (data[i+1]&0xFF);
                        if(value == 0x8000 || value == 0xFF00 || value == 0x8200 || value == 0x0001 ||
                                value == 0xFFF0) {
                            Log.d("DEBUGGING", "Skipping value: " + Integer.toString(value));
                               dataToAdd[i/2] = new DataPoint(count, 1000);
                            count++;
                            continue;
                        }
                        dataToAdd[i/2] = new DataPoint(count, value);
                        count++;
                    }

                    publishProgress(dataToAdd);

                } catch (Exception e) {
                    //TODO handle exception
                    e.printStackTrace();


                }
            }
        }

        @Override
        protected void onProgressUpdate(DataPoint... dp) {
            s.appendData(dp[0], false, 10000);

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


