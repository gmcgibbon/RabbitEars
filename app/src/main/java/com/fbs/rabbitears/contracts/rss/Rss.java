package com.fbs.rabbitears.contracts.rss;

/**
 * RSS Feed Serialization Contract
 */
public class Rss
{
    public String source;

    public Channel channel;

    public boolean isValid()
    {
        return channel != null
                && source != null
                && channel.hasItems();
    }
}
