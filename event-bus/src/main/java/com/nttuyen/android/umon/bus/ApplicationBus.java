package com.nttuyen.android.umon.bus;

import android.app.Application;

/**
 * Created by nttuyen on 2/19/15.
 */
public class ApplicationBus extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bus.defaultContext = this.getApplicationContext();
    }

    @Override
    public void onTerminate() {
        Bus.defaultContext = null;
        super.onTerminate();
    }
}
