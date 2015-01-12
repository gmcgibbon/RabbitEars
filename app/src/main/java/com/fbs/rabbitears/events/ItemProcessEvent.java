package com.fbs.rabbitears.events;

import com.fbs.rabbitears.models.FeedItem;

import java.util.List;

/**
 * Item Process Event
 */
public class ItemProcessEvent extends Event<List<FeedItem>>
{
    /**
     * Constructor
     * @param items List of FeedItem items processed
     */
    public ItemProcessEvent(List<FeedItem> items)
    {
        super(items);
    }

    /**
     * Constructor
     * @param exception Exception process items exception
     */
    public ItemProcessEvent(Exception exception)
    {
        super(exception);
    }
}
