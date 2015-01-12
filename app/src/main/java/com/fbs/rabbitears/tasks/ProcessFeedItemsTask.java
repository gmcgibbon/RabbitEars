package com.fbs.rabbitears.tasks;

import android.os.AsyncTask;

import com.fbs.rabbitears.events.ItemProcessEvent;
import com.fbs.rabbitears.helpers.ModelHelper;
import com.fbs.rabbitears.models.Feed;
import com.fbs.rabbitears.models.FeedItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Process Feed Items Async
 */
public class ProcessFeedItemsTask extends AsyncTask<String, Void, List<FeedItem>>
{
    private Feed origin;

    /**
     * Constructor
     * @param origin Feed associative parent of images
     */
    public ProcessFeedItemsTask(Feed origin)
    {
        this.origin = origin;
    }

    /**
     * Download image in background
     * @param xmlStrings String... downloaded xml to process
     * @return List of FeedItem items from xml and database
     */
    @Override
    protected List<FeedItem> doInBackground(String... xmlStrings)
    {
        List<FeedItem> items = new ArrayList<FeedItem>();

        for (String xml : xmlStrings)
        {
            try
            {
                items.addAll(origin.getItems());

                items.addAll(Arrays.asList(ModelHelper.feedItemsFromXml(origin, xml)));
            }
            catch (Exception e)
            {
                EventBus.getDefault().post(new ItemProcessEvent(e));
            }
        }

        return items;
    }

    /**
     * Post any data on task complete
     * @param items List of FeedItem database and cached items
     */
    @Override
    protected void onPostExecute(List<FeedItem> items)
    {
        super.onPostExecute(items);

        EventBus.getDefault().post(new ItemProcessEvent(items));
    }
}
