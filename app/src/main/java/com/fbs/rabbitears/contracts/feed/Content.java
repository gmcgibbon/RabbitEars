package com.fbs.rabbitears.contracts.feed;

import com.google.gson.annotations.SerializedName;

/**
 * Content Serialization Contract
 */
public class Content
{
    @SerializedName("@type")
    public String type;

    @SerializedName("$")
    public String value;
}
