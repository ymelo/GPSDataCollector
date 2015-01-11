package com.ymelo.gpsdatacollector.app.utils;

import android.support.v4.app.Fragment;

import java.lang.reflect.Field;

/**
 * Created by yohann on 10/01/15.
 */
public class FragmentFix extends Fragment {
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
