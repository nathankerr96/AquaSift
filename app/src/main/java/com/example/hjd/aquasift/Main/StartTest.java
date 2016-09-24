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

            /*
    private final BroadcastReceiver usb_reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Log.d("DEBUGGING", "Success!");
                            UsbInterface usbInterface = device.getInterface(0);
                            UsbEndpoint endpoint0 = usbInterface.getEndpoint(0);
                            UsbEndpoint endpoint1 = usbInterface.getEndpoint(1);

                            byte[] to_send = {0x0A};
                            byte[] buffer = new byte[200];

                            UsbDeviceConnection connection = manager.openDevice(device);
                            connection.claimInterface(usbInterface, true);


                            Log.d("DEBUGGING", "ENDPOING0:" + endpoint0.getType());

                            connection.controlTransfer(0x21, 0x22, 0x00, 1, null, 0, 0);
                            connection.controlTransfer(0x21, 0, 0, 1, null, 0, 0); // reset interface

                            //Should be included in SIO reset above?
                            //connection.controlTransfer(0x40, 0, 1, index, null, 0, 0); // clear Rx
                            //connection.controlTransfer(0x40, 0, 2, index, null, 0, 0); // clear Tx

                            //Set flow control to none (should be done in reset)
                            //connection.controlTransfer(0x40, 0x02, 0x0000, index, null, 0, 0); //

                            //set baud rate
                            connection.controlTransfer(0x21, 0x03, 0x000D, 1, null, 0, 0);
                            connection.controlTransfer(0x21, 0x04, 0x0008, 1, null, 0, 0);


                            Log.d("DEBUGGING", "Device Class: " + Integer.toString(device.getDeviceClass()));

                            if (connection.bulkTransfer(endpoint1, to_send, to_send.length, 1000) == -1) {
                                Log.d("DEBUGGING", "Error on Transfer to device");
                            }
                            if (connection.bulkTransfer(endpoint0, buffer, 200, 4000) == -1 ) {
                                Log.d("DEBUGGING", "Error on Transfer from device");
                            }



                            Log.d("DEBUGGING", Arrays.toString(buffer));

                        }
                    } else {
                        Log.d("DEBUGGING", "permission denied for device " + device);
                    }
                }
            }
        }
    };
    */


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



        /*
        String ACTION_USB_Permission = "com.android.example.USB_PERMISSION";
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_Permission), 0);
        FTDriver mSerial = new FTDriver((UsbManager) getSystemService(Context.USB_SERVICE));
        mSerial.setPermissionIntent(permissionIntent);



        mSerial.begin(FTDriver.BAUD230400);

        byte[] buff = new byte[20];


        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSerial.read(buff);
        Log.d("DEBUGGING", "Old Data: " + Arrays.toString(buff));

        //mSerial.write("1 B\r".getBytes());
        //mSerial.write(new byte[] {0x31, 0x20, 0x42, 0X0D});
        mSerial.write(new byte[]{0x54});
        mSerial.read(buff);
        Log.d("DEBUGGING", Arrays.toString(buff));

        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mSerial.read(buff);

        //Log.d("DEBUGGING", Arrays.toString(buff));


        */

                /*
        UsbEndpoint usbEndpoint = usbInterface.getEndpoint(0);
        UsbDeviceConnection connection = manager.openDevice(device);

        connection.claimInterface(usbInterface, true);
        connection.

        */




        //raw_data = new int[] {0x8000,0x0803,0x07A5,0x05E5,0x0565,0x0589,0x0577,0x057B,0x057B,0x057A,0x057A,0xFF00,0x8200,0x0001,0x057A,0x057B,0x0584,0x058E,0x0598,0x05A3,0x05AD,0x05B7,0x05C1,0x05CB,0x05D6,0x05E0,0x05EA,0x05F5,0x05FE,0x0608,0x0613,0x061D,0x0628,0x0632,0x063C,0x0646,0x0650,0x065A,0x0665,0x066F,0x0679,0x0684,0x068E,0x0698,0x06A2,0x06AC,0x06B7,0x06C1,0x06CB,0x06D5,0x06DF,0x06E9,0x06F4,0x06FE,0x0709,0x0713,0x071E,0x0727,0x0732,0x073C,0x0746,0x0751,0x075B,0x0765,0x076F,0x0779,0x0784,0x078F,0x0799,0x07A4,0x07AE,0x07B8,0x07C2,0x07CC,0x07D7,0x07E1,0x07EB,0x07F6,0x0800,0x080B,0x0815,0x0820,0x082A,0x0835,0x083F,0x0848,0x0852,0x085C,0x0866,0x0870,0x087B,0x0885,0x0890,0x089A,0x08A5,0x08AF,0x08B9,0x08C4,0x08CE,0x08D7,0x08E1,0x08EB,0x08F5,0x08FF,0x090A,0x0914,0x091E,0x0929,0x0934,0x093E,0x0949,0x0953,0x095D,0x0967,0x0970,0x097A,0x0984,0x098E,0x0999,0x09A4,0x09AE,0x09B9,0x09C3,0x09CD,0x09D8,0x09E2,0x09EC,0x09F6,0x0A00,0x0A0A,0x0A14,0x0A1E,0x0A29,0x0A33,0x0A3E,0x0A48,0x0A53,0x0A5D,0x0A67,0xFF00,0xFFF0};

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


