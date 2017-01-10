package com.example.hjd.aquasift.Main;

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
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
        usbHelper.setSweepStartVoltage(-200); //600
        usbHelper.setSweepEndVoltage(600); //-300
        usbHelper.setSweepRate(100); //900
        usbHelper.setDataRate(5);
        usbHelper.setCyclic(1);
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

        /*
        int numCycles = usbHelper.getNumCycles();
        if (usbHelper.isCyclic() == 1) {
            numCycles *= 2;
        }

        dataList = new ArrayList<>();
        lineGraphSeriesList = new ArrayList<>(numCycles);


        //LinearSweepTask runTest = new LinearSweepTask(this, usbHelper, graph);
        //runTest.execute();

        */

        LinearSweepTask runLinearSweep = new LinearSweepTask(usbHelper, this,
                this.getApplicationContext());
        runLinearSweep.execute();




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



    /*
    private class RunTest extends AsyncTask<String, Integer, LineGraphSeries> {

        int numCycles;

        boolean nextSweep;

        int currentList;

        boolean pickupNext = false;

        List<DataPoint> graphBuffer = new ArrayList<>();

        int lastAddedToPlot;

        ProgressDialog pdialog;

        int startVoltage;
        int endVoltage;
        float voltageIncrement;
        float currentVoltage;

        int gainResistor;


        protected void onPreExecute() {
            currentList = -1;
            numCycles = usbHelper.getNumCycles();

            gainResistor = usbHelper.getGainResistor();

            usbHelper.startLinearSweep();

            pdialog = new ProgressDialog(StartTest.this, ProgressDialog.STYLE_HORIZONTAL);
            pdialog.setTitle("Running Test");
            pdialog.setCancelable(false);
            if(usbHelper.getDepositionTime() != 0) {
                //TODO Add Progress bar for deposition period
            }

            pdialog.setMessage("Sweep 1");
            if(!pdialog.isShowing()) {
                pdialog.show();
            }

        }

        protected LineGraphSeries<DataPoint> doInBackground(String... params) {

            voltageIncrement = usbHelper.getSweepVoltageIncrement();
            //voltageIncrement = 900/200;
            startVoltage = usbHelper.getSweepStartVoltage();

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
                        continue;
                    }
                    if (value == 0x8200) { //next sequence indicator
                        pickupNext = true;
                        if (currentList >= 0) {
                            publishProgress(currentList);
                        }
                        continue;
                    }
                    if (value == 0xFF00 || value == 0x8000) {
                        continue;
                    }
                    if (value == 0xFFF0) {
                        publishProgress(currentList);

                        return null; //end
                    }

                    //SKIP DEPOSITION PERIOD
                    if(currentList < 0) {
                        continue;
                    }

                    dataList.get(currentList).add(value);



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
        protected void onProgressUpdate(Integer... passedValue) {

            int value = passedValue[0].intValue();
            float voltage = passedValue[1].floatValue();
            int seriesNum = passedValue[2].intValue();


            DataPoint dp = new DataPoint(voltage, value);
            lineGraphSeriesList.get(seriesNum).appendData(dp, false, 100000);


            int listToAdd = passedValue[0];
            int listSize = dataList.get(listToAdd).size();
            pdialog.setProgress(100*(listToAdd+1)/(numCycles));

            currentVoltage = -300;

            int max = 0;
            int min = 0;

            if(listToAdd % 2 == 0) {
                int last_added = 0;
                for(int i=0; i<listSize; i++) {
                    int oldDataElement = dataList.get(listToAdd).get(i);
                    int dataElement = (int)((oldDataElement-2047) * (3.3/4096) / gainResistor * 1000000);
                    dataList.get(listToAdd).set(i, dataElement);
                    if (dataElement > max) {
                        max = dataElement;
                    }
                    if (dataElement < min) {
                        min = dataElement;
                    }
                    Log.d("DEBUGGING", "Current Voltage: " + Float.toString(currentVoltage));
                    Log.d("DEBUGGING", "Data Point: " + Integer.toString(dataElement));

                    if (last_added - dataElement != 0 ) {
                        lineGraphSeriesList.get(listToAdd).appendData(new DataPoint((int) currentVoltage,
                                dataElement), false, 200000);
                        last_added = dataElement;
                    }
                    currentVoltage += voltageIncrement;
                }
                max = (int) (max * 1.1);
                min -= 5;
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(-40);
                graph.getViewport().setMaxY(60);
            } else {
                int lastAdded = 0;
                for(int i=listSize-1; i>=0; i--) {
                    int oldDataElement = dataList.get(listToAdd).get(i);
                    int dataElement = (int)((oldDataElement-2047) * (3.3/4096) / gainResistor * 1000000);
                    dataList.get(listToAdd).set(i, dataElement);
                    if (lastAdded - dataElement !=0) {
                        lineGraphSeriesList.get(listToAdd).appendData(new DataPoint((int) currentVoltage,
                                dataElement), false, 200000);
                        lastAdded = dataElement;
                    }
                    currentVoltage += voltageIncrement;
                }
            }

            graph.addSeries(lineGraphSeriesList.get(listToAdd));




            pdialog.setMessage("Sweep " + Integer.toString(listToAdd+2));

        }

        @Override
        protected void onPostExecute(LineGraphSeries s) {
            Toast.makeText(getBaseContext(), "Finished AsyncTask!", Toast.LENGTH_SHORT).show();
            save_data_button.setVisibility(View.VISIBLE);

            pdialog.dismiss();

            //graph.addSeries(s);
        }
    }
    */

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


