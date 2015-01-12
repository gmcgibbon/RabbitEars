package com.fbs.rabbitears.contracts.feed;

import com.google.gson.annotations.SerializedName;

/**
 * Link Serialization Contract
 */
public class Link
{
    @SerializedName("@href")
    public String href;

    @SerializedName("@rel")
    public String rel;

    @SerializedName("@type")
    public String type;

    public boolean isEnclosure()
    {
        return rel.equals("enclosure");
    }
}
