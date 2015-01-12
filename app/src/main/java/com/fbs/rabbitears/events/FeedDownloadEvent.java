package com.fbs.rabbitears.events;

/**
 * Feed Download Event
 */
public class FeedDownloadEvent extends Event<String>
{
    /**
     * Constructor
     * @param xml String downloaded feed xml data
     */
    public FeedDownloadEvent(String xml)
    {
        super(xml);
    }

    /**
     * Constructor
     * @param exception Exception download feed exception
     */
    public FeedDownloadEvent(Exception exception)
    {
        super(exception);
    }
}
