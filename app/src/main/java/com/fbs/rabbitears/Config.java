package com.fbs.rabbitears;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.fbs.rabbitears.models.Feed;
import com.fbs.rabbitears.utils.Size;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Application Configuration
 */
public class Config
{
    private static final Context CONTEXT;
    private static final Resources RESOURCES;
    private static final SharedPreferences PREFERENCES;
    private static final SharedPreferences.Editor EDITOR;
    private static final HashMap<String, String> KEYS;

    /**
     * Device DPI options
     */
    public enum Dpi
    {
        LDPI, MDPI, HDPI, XHDPI, XXHDPI, XXXHDPI
    }

    /**
     * Static Initializer
     */
    static
    {
        CONTEXT = RabbitEars.getContext();

        RESOURCES = CONTEXT.getResources();

        PREFERENCES = PreferenceManager.getDefaultSharedPreferences(CONTEXT);

        EDITOR = PREFERENCES.edit();

        KEYS = new HashMap<String, String>()
        {
            {
                put("feed",      Feed.class.getCanonicalName());
                put("feed_item", Feed.class.getCanonicalName());
            }
        };
    }

    /**
     * Get app resources
     * @return Resources
     */
    public static Resources getResources()
    {
        return RESOURCES;
    }

    /**
     * Get date format to display dates in
     * @return SimpleDateFormat date display format
     */
    public static SimpleDateFormat getDisplayDateFormat()
    {
        return new SimpleDateFormat(RESOURCES.getString(R.string.display_date_format));
    }

    /**
     * Get device DPI classification
     * @return Dpi device DPI
     */
    public static Dpi getDeviceDpi()
    {
        Dpi   info;
        float density = getResources().getDisplayMetrics().density;

        if (density <= 0.75)
        {
            info = Dpi.LDPI;
        }
        else if (density <= 1.0)
        {
            info = Dpi.MDPI;
        }
        else if (density <= 1.5)
        {
            info = Dpi.HDPI;
        }
        else if (density <= 2.0)
        {
            info = Dpi.XHDPI;
        }
        else if (density <= 3.0)
        {
            info = Dpi.XXHDPI;
        }
        else
        {
            info = Dpi.XXXHDPI;
        }

        return info;
    }

    /**
     * Get icon size based on device DPI
     * @return Size of Integer, Integer icon size
     */
    public static Size<Integer, Integer> getIconSize()
    {
        Size<Integer, Integer> dim = null;
        Dpi dpi = getDeviceDpi();

        switch (dpi) // assign based on device's DPI
        {
            case LDPI:
                dim = new Size<Integer, Integer>(36, 36);
                break;
            case MDPI:
                dim = new Size<Integer, Integer>(48, 48);
                break;
            case HDPI:
                dim = new Size<Integer, Integer>(72, 72);
                break;
            case XHDPI:
                dim = new Size<Integer, Integer>(96, 96);
                break;
            case XXHDPI:
                dim = new Size<Integer, Integer>(144, 144);
                break;
            case XXXHDPI:
                dim = new Size<Integer, Integer>(192, 192);
                break;
        }

        return dim;
    }

    /**
     * Gets current app run count
     * @return Long run count or -1
     */
    public static long getRunCount()
    {
        return getPreferences().getLong(getResources().getString(R.string.pref_run_count_key), -1);
    }

    /**
     * Add 1 to app run count
     * @return Long incremented run count
     */
    public static long incrementRunCount()
    {
        long runCount = getPreferences().getLong(getResources().getString(R.string.pref_run_count_key), 0);

        getEditor().putLong(getResources().getString(R.string.pref_run_count_key), ++runCount);

        getEditor().commit();

        return runCount;
    }

    /**
     * Determines if first run
     * @return True if first run, false if not
     */
    public static boolean isFirstRun()
    {
        return getRunCount() == 1;
    }

    /**
     * Application-wide Shared Preferences Viewer
     * @return SharedPreferences app prefs
     */
    public static SharedPreferences getPreferences()
    {
        return PREFERENCES;
    }

    /**
     * Application-wide Shared Preferences Editor
     * @return SharedPreferences app prefs editor
     */
    public static SharedPreferences.Editor getEditor()
    {
        return EDITOR;
    }

    /**
     * Get key value for setting and getting data from intents
     * @param key String key
     * @param defaultValue String default value if key does not exist
     * @return String key value
     */
    public static String getKeyValue(String key, String defaultValue)
    {
        String value = defaultValue;

        if (KEYS.containsKey(key))
        {
            value = KEYS.get(key);
        }

        return value;
    }

    /**
     * Get key value for setting and getting data from intents
     * @param key String key
     * @return String key value or null
     */
    public static String getKeyValue(String key)
    {
        return getKeyValue(key, null);
    }
}
