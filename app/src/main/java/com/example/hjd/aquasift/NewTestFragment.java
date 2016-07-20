package com.example.hjd.aquasift;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
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

import com.example.hjd.aquasift.CustomAdapters.CustomTestAdapter;

import org.w3c.dom.Text;

import java.util.List;


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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.available_tests, android.R.layout.simple_spinner_item);
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

        //Get custom test EditTexts using array defined in intergers.xml
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

        final Button start_test_button = (Button) view.findViewById(R.id.start_test_button);
        start_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start_test_intent = new Intent(getActivity(), StartTest.class);

                String[] commands = {"1", "0", "3", "4"};

                start_test_intent.putExtra(MainActivity.COMMANDS_EXTRA, commands);

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
}
