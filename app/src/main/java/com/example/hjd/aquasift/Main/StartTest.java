package com.example.hjd.aquasift.Main;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.usb.UsbManager;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.hjd.aquasift.Misc.DbHelper;
import com.example.hjd.aquasift.Misc.LinearSweepTask;
import com.example.hjd.aquasift.Misc.UsbHelper;
import com.example.hjd.aquasift.R;
import com.jjoe64.graphview.GraphView;


public class StartTest extends AppCompatActivity {

    Button save_data_button;
    GraphView graph;


    UsbManager manager;
    UsbHelper usbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        Bundle b = getIntent().getExtras();
        int[] commands = b.getIntArray(MainActivity.COMMANDS_EXTRA);

        int command1 = commands[0];
        int command2 = commands[1];
        int command3 = commands[2];
        int command4 = commands[3];


        manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        usbHelper = new UsbHelper(manager);
        int ret = usbHelper.begin();
        Log.d("DEBUGGING", Integer.toString(ret));

        usbHelper.setNumElectrodes(3);
        //usbHelper.setDataRate(50);
        usbHelper.enableDeposition(1);
        usbHelper.setDepositionTime(1000);
        usbHelper.setDepositionVoltage(600);
        usbHelper.setQuietTime(0);
        usbHelper.setSweepStartVoltage(-500); //600
        usbHelper.setSweepEndVoltage(500); //-300
        usbHelper.setSweepRate(900); //900
        usbHelper.setDataRate(5);
        usbHelper.setCyclic(0); //1
        usbHelper.setNumCycles(1);
        usbHelper.getSettings();


        graph = (GraphView) findViewById(R.id.graph);
        /*
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(-500);
        graph.getViewport().setMaxX(500);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(200);
        */


        ProgressDialog progressDialog = new ProgressDialog(this);
        LinearSweepTask runLinearSweep = new LinearSweepTask(usbHelper, this,
                this.getApplicationContext(), progressDialog);
        runLinearSweep.execute();




        save_data_button = (Button) findViewById(R.id.save_data_button);
        save_data_button.setVisibility(View.INVISIBLE);

/*
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
        */
    }



}


