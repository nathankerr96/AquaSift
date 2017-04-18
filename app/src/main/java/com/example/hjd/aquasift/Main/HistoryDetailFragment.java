package com.example.hjd.aquasift.Main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hjd.aquasift.Misc.UploadDataTask;
import com.example.hjd.aquasift.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryDetailFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    private String entryId;
    private String date;
    private String latitude;
    private String longitude;
    private String testType;
    private String peakValues;
    private String concentration;

    public HistoryDetailFragment() {
        // Required empty public constructor
    }


    public static HistoryDetailFragment newInstance(String[] params) {
        HistoryDetailFragment fragment = new HistoryDetailFragment();
        Bundle args = new Bundle();

        args.putStringArray("info", params);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] params = getArguments().getStringArray("info");
        entryId = params[0];
        date = params[1];
        latitude = params[2];
        longitude = params[3];
        testType = params[4];
        peakValues = params[5];
        concentration = params[6];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_detail, container, false);

        Button uploadDataButton = (Button) view.findViewById(R.id.upload_data_button);
        uploadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] dataToUpload = new String[] {
                        date, latitude, longitude, testType, peakValues, concentration
                };
                UploadDataTask uploadDataTask = new UploadDataTask(dataToUpload);
                uploadDataTask.execute();
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
