package com.example.hjd.aquasift.CustomAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hjd.aquasift.Main.MainActivity;
import com.example.hjd.aquasift.R;


/**
 * Implements a Custom Adapter for use in the drawer.  Each item includes a picture and string.
 * The format of the item is controlled in drawer_item.xml
 */
public class DrawerListAdapter extends BaseAdapter{

    Context context;
    private static LayoutInflater inflater = null;

    String[] fragment_names;
    int[] image_ids;

    //On creation, store fragment names and image ids for later use
    public DrawerListAdapter(MainActivity mainActivity, String[] passed_name_list,
                             int[] passed_img_ids) {

        fragment_names = passed_name_list;
        context = mainActivity;
        image_ids = passed_img_ids;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Helper class to hold objects in each drawer_item
    //TODO Figure out why this is used
    static class ViewHolder {
        private TextView fragment_name;
        private ImageView fragment_image;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        //TODO Figure out what convertView is and why it is null?
        if(convertView == null) {
            //Create new view to hold drawer_item
            View row_view;
            row_view = inflater.inflate(R.layout.drawer_item, parent, false);

            //Set Textview to name of fragment (creates in order of passed array)
            viewHolder.fragment_name = (TextView) row_view.findViewById(R.id.drawer_text);
            viewHolder.fragment_name.setText(fragment_names[position]);

            //Set Image to corresponding image (must be in same position as above)
            viewHolder.fragment_image = (ImageView) row_view.findViewById(R.id.drawer_image);
            viewHolder.fragment_image.setImageResource(image_ids[position]);


            return row_view;

        } else {
            //TODO Sometimes returns this, is that OK? Can not return null!
            return convertView;
        }
    }

    //Rest of functions had to be overwritten when extending BaseAdapter
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
        return fragment_names.length;
    }

}
