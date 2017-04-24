package com.example.hjd.aquasift.Main;

import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hjd.aquasift.CustomAdapters.DrawerListAdapter;
import com.example.hjd.aquasift.R;

public class MainActivity extends AppCompatActivity
    implements HistoryFragment.OnFragmentInteractionListener,
                NewTestFragment.OnFragmentInteractionListener,
                HistoryDetailFragment.OnFragmentInteractionListener{

    public final static String COMMANDS_EXTRA = "com.example.hjd.aquasift.COMMANDS_EXTRA";

    private String[] fragment_names;
    private DrawerLayout drawer_layout;
    private ListView drawer_list;
    private ActionBarDrawerToggle drawer_toggle;
    private CharSequence drawer_title;
    private CharSequence current_title;
    private final String FRAGMENT_NUMBER = "fragment_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment_names = getResources().getStringArray(R.array.fragment_names);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_list = (ListView) findViewById(R.id.left_drawer);

        //Cannot directly get array of drawable ids, use this workaround
        TypedArray ar = getResources().obtainTypedArray(R.array.fragment_pictures);
        int ar_length = ar.length();
        int[] fragment_pictures = new int[ar_length];
        for(int i=0; i<ar_length; i++) {
            fragment_pictures[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();

        //Uses custom adapter class
        drawer_list.setAdapter(new DrawerListAdapter(this, fragment_names, fragment_pictures));

        //Uses private class declared later in this file
        drawer_list.setOnItemClickListener(new DrawerItemClickListener());


        current_title = drawer_title = getTitle();
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //TODO research this and comment it
        drawer_toggle = new ActionBarDrawerToggle(this, drawer_layout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(current_title);
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                getActionBar().setTitle(drawer_title);
            }
        };

            //Enables button to open navigation drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Start initial fragment (home or history?)
        NewTestFragment start_fragment = new NewTestFragment();
        Bundle args = new Bundle();
        args.putInt(FRAGMENT_NUMBER, 1); //May be unnecessary
        start_fragment.setArguments(args);

        //Start Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, start_fragment).commit();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceStates) {
        super.onPostCreate(savedInstanceStates);
        drawer_toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        drawer_toggle.onConfigurationChanged(configuration);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawer_toggle.onOptionsItemSelected(item)) {
            return true;
        }

        //TODO HANDLE OTHER ACTION BAR ITEMS

        return super.onOptionsItemSelected(item);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        Class fragmentClass;
        Toast.makeText(getBaseContext(), ""+position, Toast.LENGTH_LONG).show();
        switch(position) {
            case 0:
                fragmentClass = NewTestFragment.class;
                break;
            /*
            case 1:
                //fragmentClass = second_class.class;
                break;
            case 2:
                //fragmentClass = third_class.class
                break;
             */
            default:
                fragmentClass = HistoryFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle args = new Bundle();
        args.putInt(FRAGMENT_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        drawer_list.setItemChecked(position, true);
        setTitle(fragment_names[position]);
        drawer_layout.closeDrawer(drawer_list);

    }

    public void onFragmentInteraction(Uri uri) {
        //can be left empty
    }




}
