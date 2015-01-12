package com.fbs.rabbitears.contracts.feed;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Entry Serialization Contract
 */
public class Entry
{
    public String title;

    public Content content;

    public String id;

    public Date published;

    public Date updated;

    @SerializedName("link")
    public List<Link> links;

    public boolean hasContent()
    {
        return content != null;
    }
}
