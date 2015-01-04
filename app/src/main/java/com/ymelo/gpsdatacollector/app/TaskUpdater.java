package com.ymelo.gpsdatacollector.app;

import android.os.Handler;

public class TaskUpdater {
	private static final int INTERVAL = 1000;
	private Handler mHandler;

	private Runnable runnable;
	public TaskUpdater(final Runnable uiUpdater) {
        runnable = new Runnable() {
            @Override
            public void run() {
                // Run the passed runnable
                uiUpdater.run();
                // Re-run it after the update interval
                mHandler.postDelayed(this, INTERVAL);
            }
        };
	}


	void startRepeatingTask() {
		runnable.run();
	}

	void stopRepeatingTask() {
		mHandler.removeCallbacks(runnable);
	}
}
