package com.example.hjd.aquasift.CustomAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hjd.aquasift.MainActivity;
import com.example.hjd.aquasift.NewTestFragment;
import com.example.hjd.aquasift.R;

public class CustomTestAdapter extends BaseAdapter{

    Context context;
    private static LayoutInflater inflater = null;

    String[] t_view_texts;
    String[] e_text_types;

    public CustomTestAdapter(NewTestFragment newTestFragment, String[] passed_t_view_texts,
                             String[] passed_e_text_types) {

        context = newTestFragment.getContext();
        t_view_texts = passed_t_view_texts;
        e_text_types = passed_e_text_types;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        private TextView textView;
        private EditText editText;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null) {
            View row_view;
            row_view = inflater.inflate(R.layout.custom_test_item, parent, false);

            viewHolder.textView = (TextView) row_view.findViewById(R.id.custom_test_t_view);
            viewHolder.textView.setText(t_view_texts[position]);

            viewHolder.editText = (EditText) row_view.findViewById(R.id.custom_test_e_text);
            //Customize EditText Here

            return row_view;

        } else {
            return convertView;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return t_view_texts.length;
    }


}
