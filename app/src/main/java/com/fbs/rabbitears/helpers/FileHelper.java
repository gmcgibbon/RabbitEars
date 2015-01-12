package com.fbs.rabbitears.helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fbs.rabbitears.RabbitEars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * File IO Helper Methods
 */
public class FileHelper
{
    private static final Context CONTEXT;
    private static final ContextWrapper WRAPPER;

    /**
     * Static Initializer
     */
    static
    {
        CONTEXT = RabbitEars.getContext();
        WRAPPER = new ContextWrapper(CONTEXT);
    }

    /**
     * Get cache image directory
     * @return File cached image directory
     */
    private static File getCacheDirectory()
    {
        return WRAPPER.getDir("ImageCache", Context.MODE_PRIVATE);
    }

    /**
     * Saves an image to image cache as PNG
     * @param name String name of image to save
     * @param bitmap Bitmap image to save
     * @return True if save was successful, false if not
     */
    public static boolean saveImageToCache(String name, Bitmap bitmap)
    {
        boolean saved = false;

        File dir  = getCacheDirectory();
        File file = new File(dir, name);

        FileOutputStream output;

        try
        {
            output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);

            output.close();

            saved = true;
        }
        catch (Exception e) { }

        return saved;
    }

    /**
     * Clear an individual image from image cache
     * @param name String name of image to remove
     * @return True if removal was successful, false if not
     */
    public static boolean clearImageFromCache(String name)
    {
        boolean deleted = false;

        File dir  = getCacheDirectory();
        File file = new File(dir, name);

        if (file.exists())
        {
            deleted = file.isFile()
                    && file.canWrite()
                    && file.delete();
        }

        return deleted;
    }

    /**
     * Clear all images from image cache
     * @return True if all images were removed, false if not
     */
    public static boolean clearImageCache()
    {
        boolean allDeleted = false;

        File dir  = getCacheDirectory();

        for (File file : dir.listFiles())
        {
            if (file.canWrite())
            {
                allDeleted = file.isFile()
                        && file.canWrite()
                        && file.delete();

                if (! allDeleted) { break; }
            }
        }

        return allDeleted;
    }

    /**
     * Load an individual image from cache
     * @param name String name of image to load
     * @return Bitmap loaded image from cache or null
     */
    public static Bitmap loadImageFromCache(String name)
    {
        Bitmap bitmap = null;

        File dir  = getCacheDirectory();
        File file = new File(dir, name);

        try
        {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e) { }

        return bitmap;
    }
}
