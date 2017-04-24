package com.example.hjd.aquasift.Main;

import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hjd.aquasift.Misc.LinearSweepTask;
import com.example.hjd.aquasift.Misc.UsbHelper;
import com.example.hjd.aquasift.R;
import com.jjoe64.graphview.GraphView;


public class StartTest extends AppCompatActivity {

    Button saveDataButton;
    GraphView graph;


    UsbManager manager;
    UsbHelper usbHelper;

    //Location
    public static double bestLat;
    public static double bestLong;
    public static float bestAccuracy;
    public static boolean listenerDefined;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        Bundle b = getIntent().getExtras();
        int[] commands = b.getIntArray(MainActivity.COMMANDS_EXTRA);

        int command1 = commands[0];
        int command2 = commands[1];
        int command3 = commands[2];
        int command4 = commands[3];

        final TextView accuracyTextView = (TextView) findViewById(R.id.results_accuracy_text);
        final TextView latTextView = (TextView) findViewById(R.id.results_lat_text);
        final TextView longTextView = (TextView) findViewById(R.id.results_long_text);

        saveDataButton = (Button) findViewById(R.id.save_data_button);
        saveDataButton.setVisibility(View.INVISIBLE);

        listenerDefined = false;
        //Location
        bestLat = -1;
        bestLong = -1;
        bestAccuracy = 10000;
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float currAccuracy = location.getAccuracy();
                Log.d("DEBUGGING", "Location Update");
                if (currAccuracy < bestAccuracy) {
                    bestLat = location.getLatitude();
                    bestLong = location.getLongitude();
                    bestAccuracy = currAccuracy;
                    accuracyTextView.setText(Float.toString(bestAccuracy));
                    latTextView.setText(Double.toString(bestLat));
                    longTextView.setText(Double.toString(bestLong));
                    Log.d("DEBUGGING", "Location Update: " + Float.toString(bestAccuracy));
                }
                if (bestAccuracy <= 42 && listenerDefined) {
                    locationManager.removeUpdates(this);
                    saveDataButton.setVisibility(View.VISIBLE);
                    saveDataButton.setText(R.string.save_data_button);
                    saveDataButton.setEnabled(true);

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
        Criteria criteria = new Criteria();
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        try {
            locationManager.requestLocationUpdates(0,0,criteria,locationListener,null);
        } catch (SecurityException e) {
            Log.d("DEBUGGING", "Security Exception in Location updates");
            e.printStackTrace();
        }

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        usbHelper = new UsbHelper(manager);
        int ret = usbHelper.begin();
        Log.d("DEBUGGING", Integer.toString(ret));

        usbHelper.setNumElectrodes(3);
        //usbHelper.setDataRate(50);
        usbHelper.enableDeposition(1);
        usbHelper.setDepositionTime(1000);
        usbHelper.setDepositionVoltage(600);
        usbHelper.setQuietTime(0); //Out of range?
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







    }



}


