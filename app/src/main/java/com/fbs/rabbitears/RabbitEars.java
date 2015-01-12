package com.fbs.rabbitears;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.activeandroid.ActiveAndroid;
import com.fbs.rabbitears.helpers.ViewHelper;

/**
 * Rabbit Ears Application
 */
public class RabbitEars extends Application
{
    private static Context context;

    /**
     * Initialize static app context, initialize database,
     * and set default values of preference fragments
     */
    @Override
    public void onCreate()
    {
        super.onCreate();

        context = this;

        ActiveAndroid.initialize(this);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_feed_item, false);

        Config.incrementRunCount();
    }

    /**
     * Get application context
     * @return Context app context
     */
    public static Context getContext()
    {
        return context;
    }
}
