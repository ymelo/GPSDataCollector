package com.ymelo.gpsdatacollector.app;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.ymelo.gpsdatacollector.app.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by yohann on 04/01/15.
 */
public class TripListFragment extends ListFragment {
    public static final String TAG = "TripListFragment";
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.trip_list_fragment, container, false);
//        return view;
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File[] fileList = FileUtils.getFileList(getActivity());
        String[] nameList = new String[fileList.length];
        int i = 0;
        for (File f : fileList) {
            nameList[i] = f.getName();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, nameList);
        setListAdapter(adapter);
        setListShown(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        try {
            Toast.makeText(getActivity(), FileUtils.getFileContent(getActivity(), (String) getListAdapter().getItem(position)), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
