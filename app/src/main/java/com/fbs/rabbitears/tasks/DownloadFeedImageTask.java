package com.fbs.rabbitears.tasks;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.fbs.rabbitears.Config;
import com.fbs.rabbitears.R;
import com.fbs.rabbitears.helpers.FileHelper;
import com.fbs.rabbitears.models.Feed;
import com.fbs.rabbitears.utils.Size;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Feed Image Download Async
 */
public class DownloadFeedImageTask extends AsyncTask<ImageView, Void, Bitmap>
{
    private ImageView[]    views;
    private Feed           origin;
    private ProgressDialog dialog;

    /**
     * Constructor
     * @param origin Feed origin of request for image
     * @param dialog ProgressDialog dialog to close when finished
     */
    public DownloadFeedImageTask(Feed origin, ProgressDialog dialog)
    {
        this.origin = origin;
        this.dialog = dialog;
    }

    /**
     * Download image in background
     * @param views ImageView... image views to update on completion
     * @return Bitmap downloaded feed image
     */
    @Override
    protected Bitmap doInBackground(ImageView... views)
    {
        String fileName = origin.toString();

        Bitmap image = FileHelper.loadImageFromCache(fileName); // first try load from cache

        try
        {
            this.views = views;

            if (image == null) // if cache load was unsuccessful
            {
                image = downloadImage(new URL(origin.image)); // download and save to cache

                FileHelper.saveImageToCache(fileName, image);
            }
        }
        catch (Exception e)
        {
            // assign default image on error
            image = BitmapFactory.decodeResource(Config.getResources(), R.drawable.ic_feed);
        }

        return image;
    }

    /**
     * Update views on complete
     * @param bitmap Bitmap scaled downloaded feed image
     */
    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        super.onPostExecute(bitmap);

        origin.setCachedImage(bitmap);

        for (ImageView view : views)
        {
            try
            {
                view.setImageBitmap(bitmap);
            }
            catch (Exception e) { }
        }

        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    /**
     * Download Image resource as scaled bitmap
     * @param imageUrl URL address of image resource
     * @return Bitmap downloaded image
     * @throws IOException
     */
    private Bitmap downloadImage(URL imageUrl)
            throws IOException
    {
        InputStream urlStream    = imageUrl.openStream();
        Bitmap      urlImage     = BitmapFactory.decodeStream(urlStream);
        Size<Integer, Integer> iconSize  = Config.getIconSize();
        Size<Integer, Integer> imageSize = new Size<Integer, Integer>(urlImage.getWidth(), urlImage.getHeight());

        while (imageSize.height > iconSize.height && imageSize.width > iconSize.width)
        {
            imageSize.height = (int)(imageSize.width  /1.2);
            imageSize.width  = (int)(imageSize.width  /1.2);
        }

        return Bitmap.createScaledBitmap(urlImage, imageSize.width,  imageSize.height, false);
    }
}
