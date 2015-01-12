package com.fbs.rabbitears.tasks;

import android.os.AsyncTask;

import com.fbs.rabbitears.Config;
import com.fbs.rabbitears.R;
import com.fbs.rabbitears.events.FeedDownloadEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import de.greenrobot.event.EventBus;

/**
 * Download Feed Async
 */
public class DownloadFeedTask extends AsyncTask<String, Void, String>
{
    /**
     * Download feed in background
     * @param urls String... urls to download
     * @return String downloaded xml
     */
    @Override
    protected String doInBackground(String... urls)
    {
        try
        {
            for (String url : urls)
            {
                String downloaded = downloadFeed(new URL(url));
                String doctype    = downloaded.substring(0, 38).toLowerCase().replace('\'', '\"');

                if (doctype.startsWith("<?xml"))
                {
                    return downloaded;
                }
                else
                {
                    String message = Config.getResources().getString(R.string.feed_url_error_xml);
                    throw new Exception(message);
                }
            }
        }
        catch (MalformedURLException e)
        {
            String message = Config.getResources().getString(R.string.feed_url_error_url);

            EventBus.getDefault().post(
                    new FeedDownloadEvent(
                            new Exception(message, e)
                    )
            );
        }
        catch (Exception e)
        {
            EventBus.getDefault().post(new FeedDownloadEvent(e));
        }

        return null;
    }

    /**
     * Post downloaded xml on success if not null
     * @param feedXml String feed xml
     */
    @Override
    protected void onPostExecute(String feedXml)
    {
        if (feedXml != null)
        {
            EventBus.getDefault().post(new FeedDownloadEvent(feedXml));
        }
    }

    /**
     * Download XML feed markup string
     * @param feedUrl URL comic feed address
     * @return String XML feed markup
     * @throws IOException
     */
    private String downloadFeed(URL feedUrl)
            throws IOException
    {
        StringBuilder feedBuilder = new StringBuilder();
        InputStream   urlStream = feedUrl.openStream();

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(urlStream));

        String line;
        while ((line = reader.readLine()) != null)
        {
            feedBuilder.append(line);
        }

        return feedBuilder.toString();
    }
}
