package com.example.hjd.aquasift.Main;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hjd.aquasift.Misc.TestType;
import com.example.hjd.aquasift.Misc.UsbHelper;
import com.example.hjd.aquasift.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewTestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewTestFragment extends Fragment {

    //Arrays for the custom test options
    final TextView[] t_views = new TextView[5];
    final EditText[] e_texts = new EditText[5];

    Button start_test_button;

    String ACTION_USB_Permission = "com.android.example.USB_PERMISSION";

    ListView custom_test_list;

    private OnFragmentInteractionListener mListener;

    public NewTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewTestFragment newInstance(String param1, String param2) {
        NewTestFragment fragment = new NewTestFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        } */

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_test, container, false);

        //Set up spinner to choose type of test
        Spinner select_test = (Spinner) view.findViewById(R.id.select_test_spinner);


        TestType[] savedTests = {};
        File settingsFile = new File(getContext().getFilesDir(), TestType.TESTS_FILE_NAME);
        FileInputStream fis;
        try {
            fis = new FileInputStream(settingsFile);
        } catch (FileNotFoundException e) {
            fis = null;
        }
        ObjectInputStream ois = null;
        if (fis != null) {
            try {
                ois = new ObjectInputStream(fis);
            } catch (IOException e) {
                ois = null;
                e.printStackTrace();
            }
        }
        if (ois != null) {
            try {
                savedTests = (TestType[])ois.readObject();
            } catch (IOException|ClassNotFoundException e) {
                e.printStackTrace();
            }
        }




        TypedArray defaultTestNamesTyped = view.getResources().obtainTypedArray(R.array.available_tests);
        String[] defaultTestNames = new String[defaultTestNamesTyped.length()];
        for (int i = 0; i<defaultTestNamesTyped.length(); i++) {
            defaultTestNames[i] = defaultTestNamesTyped.getString(i); //TODO only assign defaultnamestyped once

        }
        defaultTestNamesTyped.recycle();

        TypedArray defaultTestIds = view.getResources().obtainTypedArray(R.array.defaultTestIds);
        int[][] defaultTestSettings = new int[defaultTestIds.length()][];
        for (int i=0; i<defaultTestIds.length(); i++) {
            int testParamsID = defaultTestIds.getResourceId(i, 0);

            defaultTestSettings[i] = view.getResources().getIntArray(testParamsID);

        }
        defaultTestIds.recycle();

        TestType[] defaultTests = new TestType[defaultTestNames.length];
        for (int i=0; i<defaultTestIds.length(); i++) {
            defaultTests[i] = new TestType(defaultTestNames[i], defaultTestSettings[i]);
        }






        String[] test_types = new String[defaultTests.length + savedTests.length];
        for (int i=0; i<defaultTests.length; i++) {
            test_types[i] = defaultTests[i].testName;
        }



        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
         //       R.array.available_tests, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, test_types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_test.setAdapter(adapter);

        /*
        custom_test_list = (ListView) view.findViewById(R.id.custom_test_list);

        String[] t_view_texts = view.getResources().getStringArray(R.array.custom_test_texts);
        String[] e_text_types = null;

        custom_test_list.setAdapter(new CustomTestAdapter(this, t_view_texts, e_text_types));
        */


        //Get custom test TextViews using array defined in integers.xml
        //Must used TypedArray instead of IntArray (IntArray returns null when used with ids)
        TypedArray t_ids = view.getResources().obtainTypedArray(R.array.custom_test_textviews);
        for(int i=0; i < t_views.length; i++){
            t_views[i] = (TextView) view.findViewById(t_ids.getResourceId(i,0));
        }
        t_ids.recycle();

        //Get custom test EditTexts using array defined in integers.xml
        TypedArray e_ids = view.getResources().obtainTypedArray(R.array.custom_test_edittexts);
        for(int i=0; i < e_texts.length; i++) {
            e_texts[i] = (EditText) view.findViewById(e_ids.getResourceId(i,0));
        }


        select_test.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handle_spinner_selection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        start_test_button = (Button) view.findViewById(R.id.start_test_button);
        start_test_button.setEnabled(false);

        final UsbManager manager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        PendingIntent permissionIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(ACTION_USB_Permission), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_Permission);

        UsbHelper usbHelper = new UsbHelper(manager);
        if (usbHelper.checkDevices()) {

            if (!usbHelper.hasPermission()) {
                usbHelper.requestPermission(permissionIntent);
            } else {
                start_test_button.setEnabled(true);
            }
        } else {
            Log.d("DEBUGGING", "Could not find device");
        }
        getContext().registerReceiver(usbReceiver, filter);



        start_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start_test_intent = new Intent(getActivity(), StartTest.class);

                String[] commands = {"1", "0", "3", "4"};

                start_test_intent.putExtra(MainActivity.COMMANDS_EXTRA, commands);

                //Log.d("DEBUGGING", "PERMISSION: " + Boolean.toString(manager.hasPermission(targetDevice)));

                startActivity(start_test_intent);
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

    //Shows/Hides custom inputs depending on what test was selected
    private void handle_spinner_selection(int position) {
        if(position == 2) { //position == position of "Custom"
            //Custom test selected, show custom options
            for (TextView t_view : t_views) {
                t_view.setVisibility(View.VISIBLE);
            }
            for (EditText e_text : e_texts) {
                e_text.setVisibility(View.VISIBLE);
            }

        } else {

            //Non custom test selected, remove custom options if present
            for (TextView t_view : t_views) {
                t_view.setVisibility(View.GONE);
            }
            for(EditText e_text : e_texts) {
                e_text.setVisibility(View.GONE);
            }
        }
    }


    //TODO EACH CALL RESULTS IN RUNNING MULTIPLE TIMES, POSSIBLE MULTIPLE FRAGMENTS?
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_Permission.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (device != null) {
                            Log.d("DEBUGGING", "PERMISSION GRANTED");
                            start_test_button.setEnabled(true);
                        }
                    } else {
                        Log.d("DEBUGGING", "PERMISSION DENIED");
                    }
                }
            }
        }
    };
}
