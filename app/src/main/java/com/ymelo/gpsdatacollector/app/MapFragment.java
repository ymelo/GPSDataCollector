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
    private GoogleMap mMap;
    private String dataFilename;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment).commit();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setUpMapIfNeeded(mapFragment);
        // Polylines are useful for marking paths and routes on the map.
        try {
            final List<LatLng> mapData = getMapData();
            PolylineOptions po = new PolylineOptions();
            po.color(getResources().getColor(R.color.polylineColor));
            po.width(3.0f);
            po.geodesic(true).addAll(mapData);
            mMap.addPolyline(po);
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    LatLngBounds.Builder b = new LatLngBounds.Builder();
                    for (LatLng point : mapData) {
                        b.include(point);
                    }
                    LatLngBounds bounds = b.build();
                    //Change the padding as per needed
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 30);
                    mMap.animateCamera(cu);
                }
            });

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private SupportMapFragment mapFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager manager = getChildFragmentManager();
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
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Location lastNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(lastNetworkLocation != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
                Location lastGPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastGPSLocation != null) {
                    if(lastGPSLocation.getElapsedRealtimeNanos() > lastNetworkLocation.getElapsedRealtimeNanos()) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(lastNetworkLocation.getLatitude(), lastNetworkLocation.getLongitude()), 14));
                    } else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(lastGPSLocation.getLatitude(), lastGPSLocation.getLongitude()), 14));
                    }
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lastNetworkLocation.getLatitude(), lastNetworkLocation.getLongitude()), 14));
                }
            } else {
                //No simple way to compare the two different location time
                //So go for the assumption that the network location should be used
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastNetworkLocation.getLatitude(), lastNetworkLocation.getLongitude()), 14));
            }
        }
    }
    private void setUpMapIfNeeded(SupportMapFragment mapFragment) {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
//            map = ((com.google.android.gms.maps.SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap = mapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
//            	map.setOnMyLocationButtonClickListener(this);
                mapSetup();
            }
        }
    }

}
