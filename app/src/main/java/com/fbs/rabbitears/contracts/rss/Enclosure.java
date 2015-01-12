package com.fbs.rabbitears.contracts.rss;

import com.google.gson.annotations.SerializedName;

/**
 * Enclosure Serialization Contract
 */
public class Enclosure
{
    @SerializedName("@url")
    public String link;
    @SerializedName("@type")
    public String mime;
    @SerializedName("@length")
    public String length;
}
