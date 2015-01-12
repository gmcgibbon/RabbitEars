package com.fbs.rabbitears.helpers;

import com.fbs.rabbitears.contracts.rss.Channel;
import com.fbs.rabbitears.contracts.rss.Image;
import com.fbs.rabbitears.contracts.rss.Item;
import com.fbs.rabbitears.contracts.rss.Rss;
import com.fbs.rabbitears.models.Feed;
import com.fbs.rabbitears.models.FeedItem;
import com.fbs.rabbitears.models.ItemMedia;
import com.fbs.rabbitears.utils.RssParser;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Model Helper Methods
 */
public class ModelHelper
{
    /**
     * Parse model Feed from contract Rss
     * @param contract Rss contract to parse
     * @return Feed parsed data model feed
     */
    private static Feed feedFromContractClass(Rss contract)
    {
        Feed    modelFeed    = new Feed(contract.source);
        Channel contractFeed = contract.channel;

        for (Field modelField : Feed.class.getFields())
        {
            String modelFieldName = modelField.getName();

            for (Field contractField : Channel.class.getFields())
            {
                String contractFieldName = contractField.getName();

                try
                {
                    if (contractFieldName.equals(modelFieldName))
                    {
                        if (contractFieldName.equals("image"))
                        {
                            modelField.set(modelFeed, ((Image)(contractField.get(contractFeed))).url);
                        }
                        else
                        {
                            modelField.set(modelFeed, contractField.get(contractFeed));
                        }
                    }
                    else if (contractFieldName.equals("lastBuildDate") && modelFieldName.equals("updatedAt"))
                    {
                        modelField.set(modelFeed, contractField.get(contractFeed));
                    }
                }
                catch(Exception e) { }
            }
        }

        return modelFeed;
    }

    /**
     * Parse model FeedItem array from contract Rss and parent feed (for assocation)
     * @param parent Feed parent to associate with
     * @param contract Rss contract to parse
     * @return FeedItem array parsed data model items
     */
    private static FeedItem[] feedItemsFromContractClass(Feed parent, Rss contract)
    {
        FeedItem[] modelItems    = new FeedItem[contract.channel.items.size()];
        List<Item> contractItems = contract.channel.items;

        for (int i = 0; i < contractItems.size(); i++)
        {
            modelItems[i] = feedItemFromContractClass(parent, contractItems.get(i));
        }

        return modelItems;
    }

    /**
     * PArse model FeedItem from contract Rss and parent feed (for association)
     * @param parent Feed parent to associate with
     * @param contract Item contract to parse
     * @return FeedItem parsed data model item
     */
    private static FeedItem feedItemFromContractClass(Feed parent, Item contract)
    {
        FeedItem feedItem = new FeedItem(parent);

        feedItem.title       = contract.title;
        feedItem.link        = contract.link;
        feedItem.description = contract.description;

        feedItem.category    = contract.getCategoryString();
        feedItem.createdAt   = contract.getDate();
        feedItem.guid        = contract.getGuid();

        if (contract.hasEnclosure())
        {
            ItemMedia media = new ItemMedia();
            media.link   = contract.enclosure.link;
            media.mime   = contract.enclosure.mime;
            try
            {
                media.length = Long.parseLong(contract.enclosure.length);
            }
            catch (Exception e) { }

            feedItem.itemMedia = media;
        }

        return feedItem;
    }

    /**
     * Parse feed from downloaded xml
     * @param source String source url
     * @param xml String xml to parse
     * @return Feed parsed data model feed
     */
    public static Feed feedFromXml(String source, String xml)
    {
        Feed feed = null;

        try
        {
            Rss contract = parseXml(source, xml);

            feed = feedFromContractClass(contract);
        }
        catch (Exception e)
        {
            e.getCause();
        }

        return feed;
    }

    /**
     * Parse feed items from downloaded xml
     * @param parent Feed parent to associate with
     * @param xml String xml to parse
     * @return FeedItem array parsed data model items
     */
    public static FeedItem[] feedItemsFromXml(Feed parent, String xml)
    {
        FeedItem[] items = null;

        try
        {
            Rss contract = parseXml(parent.source, xml);

            items = feedItemsFromContractClass(parent, contract);
        }
        catch (Exception e)
        {
            e.getCause();
        }

        return items;
    }

    /**
     * Parse xml using serialization and manual parsing on deserialization failure
     * @param source String source url
     * @param xml String xml to parse
     * @return Rss contract class from xml
     */
    private static Rss parseXml(String source, String xml)
    {
        Rss contract = null;

        try
        {
            contract = DeserializationHelper
                    .getGsonXml().fromXml(xml, Rss.class);

            contract.source = source;
        }
        catch (Exception e)
        {
            e.getCause();
        }

        if (contract == null || ! contract.isValid())
        {
            try
            {
                contract = new Rss();

                contract.channel = DeserializationHelper
                        .getGsonXml().fromXml(
                                xml, com.fbs.rabbitears.contracts.feed.Feed.class
                        ).toChannel();

                contract.source = source;
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }

        if (contract == null || ! contract.isValid())
        {
            try
            {
                contract = new RssParser(xml).getRss();

                contract.source = source;
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }

        return contract;
    }
}
