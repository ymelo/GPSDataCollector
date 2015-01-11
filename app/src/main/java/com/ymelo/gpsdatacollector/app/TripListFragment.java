package com.ymelo.gpsdatacollector.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.ymelo.gpsdatacollector.app.utils.FileUtils;
import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by yohann on 04/01/15.
 */
public class TripListFragment extends ListFragment {
    public static final String TAG = "TripListFragment";
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

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
        ((MainActivity) getActivity()).replaceMap((String) getListAdapter().getItem(position));
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
