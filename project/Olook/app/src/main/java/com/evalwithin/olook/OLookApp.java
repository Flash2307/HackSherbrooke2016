package com.evalwithin.olook;

import android.app.Application;
import android.content.Context;

/**
 * Created by Pascal on 23/04/2016.
 */
public class OLookApp extends Application
{
    private static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        OLookApp.context = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return OLookApp.context;
    }
}
