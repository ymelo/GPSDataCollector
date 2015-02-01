package com.ymelo.gpsdatacollector.app.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by yohann on 10/01/15.
 */
public class FragmentFix extends Fragment {

    public static final String LIFECYCLE_TAG = "fragment_lifecycle";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LIFECYCLE_TAG, "OnCreate for " + ((Object) this).getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LIFECYCLE_TAG, "onResume for " + ((Object) this).getClass().getSimpleName());
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
