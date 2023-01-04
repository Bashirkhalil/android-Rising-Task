package com.example.test.appModel;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class AppController extends Application {

    private static final String mTag = AppController.class.getSimpleName();
    private static AppController mInstance;
    private static Context mContext;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static synchronized Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this ;
    }


}