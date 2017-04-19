package com.example.hjd.aquasift.CustomAdapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hjd.aquasift.Main.HistoryFragment;
import com.example.hjd.aquasift.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.Inflater;

public class HistoryAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    Context context;

    Series<DataPoint>[] s;
    String[] titles;
    String[] dates;
    String[] types;

    public HistoryAdapter(HistoryFragment historyFragment, String[] passed_titles,
                               String[] passed_dates, String[] passed_types) {

        context = historyFragment.getContext();

        //s = passed_series;
        titles = passed_titles;
        dates = passed_dates;
        types = passed_types;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    private static class ViewHolder {
        GraphView graph_view;
        TextView title_text_view;
        TextView date_text_view;
        TextView type_text_view;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();


        if (convertView == null) {

            Log.d("DEBUGGING", "HEREHRER");

            View row_view = inflater.inflate(R.layout.history_item, parent, false);

            //viewHolder.graph_view = (GraphView) row_view.findViewById(R.id.history_item_graph);
            //viewHolder.graph_view.addSeries(s[position]);

            viewHolder.title_text_view = (TextView) row_view.findViewById(R.id.history_item_title);
            viewHolder.title_text_view.setText(titles[position]);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
            String dateString = sdf.format(Long.parseLong(dates[position]));
            viewHolder.date_text_view = (TextView) row_view.findViewById(R.id.history_item_date);
            viewHolder.date_text_view.setText(dateString);

            viewHolder.type_text_view = (TextView) row_view.findViewById(R.id.history_item_type);
            viewHolder.type_text_view.setText(types[position]);

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
        return titles.length;
    }
}
