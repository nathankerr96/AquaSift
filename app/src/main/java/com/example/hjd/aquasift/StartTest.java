package com.example.hjd.aquasift;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class StartTest extends AppCompatActivity {

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

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> s = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0,1),
                new DataPoint(1,2),
                new DataPoint(2,0),
                new DataPoint(3,3),
                new DataPoint(4,3)
        });

        graph.addSeries(s);

        Button save_data_button = (Button) findViewById(R.id.save_data_button);


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

    private class SaveData implements Runnable {

        public void run() {
            DbHelper dbHelper = new DbHelper(getBaseContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DbHelper.COL_USER_ID, "343");
            values.put(DbHelper.COL_DATE, "12/23/16");

            db.insertOrThrow(DbHelper.TABLE_NAME, null, values);

            Log.d("DEBUGGING", "THREAD FINISHED");
        }

    }
}


