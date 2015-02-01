package com.ymelo.gpsdatacollector.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ymelo.gpsdatacollector.app.utils.FileUtils;
import com.ymelo.gpsdatacollector.app.utils.FragmentFix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yohann on 04/01/15.
 */
public class MapFragment extends FragmentFix {
    public static final String TAG = "MapFragment";
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private String dataFilename;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * mNeedAnimateCamera
     * If set to true, the map will be recentered on the trip
     * on the next call to fillMapData
     */
    private boolean mNeedAnimateCamera = true;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MapFragment newInstance(String dataFileName) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NUMBER, dataFileName);
        fragment.setArguments(args);
        fragment.setRetainInstance(true);
        return fragment;
    }

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        dataFilename = bundle.getString(ARG_SECTION_NUMBER);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        rootView.setBackgroundColor(getResources().getColor(android.R.color.black));
        if(savedInstanceState != null) {
            //After config change etc, simply reset the map to
            //show the whole trip
            mNeedAnimateCamera = true;
        }
        FragmentManager fm = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) fm.findFragmentByTag("googlemap");
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.container, mMapFragment, "googlemap").commit();
            mMapFragment.setRetainInstance(true);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout mapLayout = (FrameLayout) getView().findViewById(R.id.container);
        /*
        It is only possible to centre the map on the trip after it has been layed out.
         */
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fillMapData();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(activity.getString(R.string.fragment_title_map));
    }

    public List<LatLng> getMapData() throws IOException {
        if(Config.FAKE_DATA_ON_MAP) {
            ArrayList<LatLng> list = new ArrayList<LatLng>(10);
            double lat = Config.LAT;
            double lng = Config.LNG;
            for(int i = 0; i < 10 ; i++) {
                lat += 0.00200170d;
                lng += 0.00200170d;
                LatLng loc = new LatLng(lat, lng);
                list.add(loc);
            }
            return list;

        } else {
            return getMessages();
        }
    }



    public List<LatLng> getMessages() throws IOException {
        if(dataFilename == null) {
            return null;
        }
        ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(FileUtils.getFileInputStream(getActivity().getApplicationContext(), dataFilename)));

            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            double lat, lng;
            String[] split;
            while (mLine != null) {
                split = mLine.split(", ");
                if(split.length == 2) {
                    lat = Double.valueOf(split[0]);
                    lng = Double.valueOf(split[1]);
                    latLngs.add(new LatLng(lat, lng));
                    mLine = reader.readLine();
                }

            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return latLngs;
    }

    void mapSetup() {
        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
        } else {
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }
    private void setUpMapIfNeeded(SupportMapFragment mapFragment) {
        mMap = null;
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
//            map = ((com.google.android.gms.maps.SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap = mapFragment.getMap();
        }
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mapSetup();
        }
    }

    private void fillMapData() {
        setUpMapIfNeeded(mMapFragment);
        if(mNeedAnimateCamera) {
            mNeedAnimateCamera = false;
            // Polylines are useful for marking paths and routes on the map.
            try {
                final List<LatLng> mapData = getMapData();
                PolylineOptions po = new PolylineOptions();
                po.color(getResources().getColor(R.color.polylineColor));
                po.width(3.0f);
                po.geodesic(true).addAll(mapData);
                mMap.addPolyline(po);
                LatLngBounds.Builder b = new LatLngBounds.Builder();
                for (LatLng point : mapData) {
                    b.include(point);
                }
                LatLngBounds bounds = b.build();
                //Change the padding as per needed
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 30);
                mMap.animateCamera(cu);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
