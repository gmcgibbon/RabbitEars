package com.fbs.rabbitears.contracts.rss;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * RSS Channel Item Serialization Contract
 */
public class Item
{
    public String title;
    public String link;
    public String description;

    public Enclosure enclosure;
    public Guid      guid;

    public Date pubDate;
    @SerializedName("<a10>updated")
    public Date updated;

    @SerializedName("category")
    public List<Category> categories;

    public boolean hasEnclosure()
    {
        return enclosure != null;
    }

    public Date getDate()
    {
        return (pubDate != null) ?
            pubDate : updated;
    }

    public String getCategoryString()
    {
        String categoryString = null;

        if (categories != null)
        {
            StringBuilder categoryBuilder = new StringBuilder();

            for (Category cat : categories)
            {
                categoryBuilder.append(cat.value);

                if (categories.indexOf(cat) < categories.size() - 1)
                {
                    categoryBuilder.append(", ");
                }
            }

            categoryString = categoryBuilder.toString();
        }

        return categoryString;
    }

    public String getGuid()
    {
        String guidString = null;

        if (guid != null)
        {
            guidString = guid.value;
        }

        return guidString;
    }
}
