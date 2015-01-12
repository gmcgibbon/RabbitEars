package com.fbs.rabbitears.contracts.feed;

import com.fbs.rabbitears.contracts.rss.Channel;
import com.fbs.rabbitears.contracts.rss.Enclosure;
import com.fbs.rabbitears.contracts.rss.Guid;
import com.fbs.rabbitears.contracts.rss.Image;
import com.fbs.rabbitears.contracts.rss.Item;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Feed Serialization Contract
 */
public class Feed
{
    public String title;

    public String subtitle;

    public String icon;

    public Link link;

    public Date updated;

    @SerializedName("entry")
    public List<Entry> entries;

    public boolean isValid()
    {
        return title != null && entries != null;
    }

    public Channel toChannel()
    {
        Channel channel = new Channel();

        channel.title       = title;
        channel.description = subtitle;

        channel.image     = new Image();
        channel.image.url = icon;

        channel.link          = link.href;
        channel.lastBuildDate = updated;

        channel.items = new ArrayList<Item>();

        for (Entry entry : entries)
        {
            Item item = new Item();

            item.title       = entry.title;
            item.guid        = new Guid();
            item.guid.value  = entry.id;
            item.updated     = entry.updated;
            item.pubDate     = entry.published;

            if (entry.hasContent())
            {
                item.description = entry.content.value;
            }

            for (Link link : entry.links)
            {
                if (link.isEnclosure())
                {
                    item.enclosure = new Enclosure();
                    item.enclosure.length = "-1";
                    item.enclosure.link   = link.href;
                    item.enclosure.mime   = link.type;
                }
                else
                {
                    item.link = link.href;
                }
            }

            channel.items.add(item);
        }

        return channel;
    }
}
