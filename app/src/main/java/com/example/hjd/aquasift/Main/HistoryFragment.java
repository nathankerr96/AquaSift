package com.example.hjd.aquasift.Main;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hjd.aquasift.CustomAdapters.HistoryAdapter;
import com.example.hjd.aquasift.Misc.DbHelper;
import com.example.hjd.aquasift.R;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    int test_ids[];

    public HistoryFragment() {
        // Required empty public constructor
    }


    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();

        return fragment;
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceStates) {
        super.onCreate(savedInstanceStates);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);



        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DbHelper.COL_ENTRY_ID,
                DbHelper.COL_DATE,
                DbHelper.COL_LAT,
                DbHelper.COL_LONG,
                DbHelper.COL_TEST_TYPE,
                DbHelper.COL_PEAK_VALUES,
                DbHelper.COL_CONCENTRATION
        };

        Cursor c = db.query(
            DbHelper.TABLE_NAME,
            projection,
            null, //cols for WHERE
            null, //vals for WHERE
            null, //GROUP BY
            null, //FILTER BY
            null //ORDER BY
        );

        Log.d("DEBUGGING", "ENTRIES FOUND: " + c.getCount());

        final String[] titles = new String[c.getCount()];

        int count = c.getCount();

        test_ids = new int[count];
        final String[] dates = new String[count];
        final String[] lats = new String[count];
        final String[] longs = new String[count];
        final String[] types = new String[count];
        final String[] peaks = new String[count];
        final String[] concentration = new String[count];
        //Series[] series = new Series[c.getCount()];



        if(c.getCount() != 0) {


            c.moveToFirst();
            int i=0;
            while (!c.isAfterLast()) {

                //String user_id = c.getString(c.getColumnIndex(DbHelper.COL_USER_ID));


                test_ids[i] = c.getInt(c.getColumnIndex(DbHelper.COL_ENTRY_ID));

                titles[i] = Integer.toString(test_ids[i]);
                dates[i] = c.getString(c.getColumnIndex(DbHelper.COL_DATE));
                lats[i] = c.getString(c.getColumnIndex(DbHelper.COL_LAT));
                longs[i] = c.getString(c.getColumnIndex(DbHelper.COL_LONG));
                types[i] = c.getString(c.getColumnIndex(DbHelper.COL_TEST_TYPE));
                peaks[i] = c.getString(c.getColumnIndex(DbHelper.COL_PEAK_VALUES));
                concentration[i] = c.getString(c.getColumnIndex(DbHelper.COL_CONCENTRATION));

                //String raw_data_string = c.getString(c.getColumnIndex(DbHelper.COL_DATE));

                /*
                String[] raw_data = raw_data_string.split(", ");
                raw_data[0] = raw_data[0].replace("[", "");
                raw_data[raw_data.length-1] = raw_data[raw_data.length-1].replace("]", "");

                DataPoint[] data = new DataPoint[raw_data.length];

                for (int j=0; j<raw_data.length; j++) {
                        data[j] = new DataPoint(j, Integer.parseInt(raw_data[j]));
                }

                series[i] = new LineGraphSeries(data);
                */

                c.moveToNext();
                i++;
            }

        }
        c.close ();

        ListView main_history_view = (ListView) view.findViewById(R.id.history_list_view);

        main_history_view.setAdapter(new HistoryAdapter(this, titles, dates, types));
        main_history_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

                String[] itemData = new String[] {
                    titles[position], dates[position], lats[position], longs[position], types[position],
                        peaks[position], concentration[position]
                };
                HistoryDetailFragment newDetailFragment = HistoryDetailFragment.newInstance(itemData);
                fragmentTransaction.addToBackStack("Detail");

                Bundle bundle = new Bundle();
                bundle.putInt("Test_ID", test_ids[position]);

                fragmentTransaction.replace(R.id.content_frame, newDetailFragment, "detail_fragment");

                fragmentTransaction.commit();
            }
        });


        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
