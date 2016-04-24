package com.sample.jsonstoreandroid;

import android.app.Application;
import android.util.Log;

import com.worklight.wlclient.api.WLClient;

public class JSONStoreApplication extends Application {
    public void onCreate() {
        super.onCreate();
        WLClient.createInstance(this);
    }
}
