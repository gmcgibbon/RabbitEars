package com.fbs.rabbitears.contracts.rss;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * RSS Channel Serialization Contract
 */
public class Channel
{
    public String title;
    public String link;
    public String description;

    public Image image;

    public Date lastBuildDate;

    @SerializedName("item")
    public List<Item> items;

    public boolean hasImage()
    {
        return image != null;
    }

    public boolean hasItems()
    {
        return items != null && items.size() > 0;
    }
}
