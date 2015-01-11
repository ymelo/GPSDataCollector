package com.ymelo.gpsdatacollector.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yohann on 10/01/15.
 */
public class DisplayFragment extends Fragment implements TripListFragment.OnItemClickedListener{

    public static final String TAG = "DisplayFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        if(savedInstanceState == null) {
            FragmentManager manager = getFragmentManager();
            Fragment fr = manager.findFragmentByTag(TripListFragment.TAG);
            if(fr == null) {
                fr = new TripListFragment();
                ((TripListFragment)fr).listener = this;
                fr.setRetainInstance(true);
                manager.beginTransaction().add(R.id.container, fr, TripListFragment.TAG).commit();
            }
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public void replaceMap(String dataFilename) {
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment fragment;
        fragment = (MapFragment) fragmentManager.findFragmentByTag(MapFragment.TAG);
        if(fragment == null) {
            fragment = MapFragment.newInstance(dataFilename);
            fragment.setRetainInstance(true);
        } else {

        }
        fragmentManager.beginTransaction().remove(fragment)
                .add(R.id.container, fragment, MapFragment.TAG)
                .addToBackStack(null)
                .commit();
        fragment = (MapFragment) fragmentManager.findFragmentByTag(MapFragment.TAG);
    }

    @Override
    public void onFileClicked(String filePath) {
        replaceMap(filePath);
    }
}
