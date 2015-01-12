package com.fbs.rabbitears.utils;

import com.fbs.rabbitears.contracts.rss.Category;
import com.fbs.rabbitears.contracts.rss.Channel;
import com.fbs.rabbitears.contracts.rss.Enclosure;
import com.fbs.rabbitears.contracts.rss.Guid;
import com.fbs.rabbitears.contracts.rss.Image;
import com.fbs.rabbitears.contracts.rss.Item;
import com.fbs.rabbitears.contracts.rss.Rss;
import com.fbs.rabbitears.helpers.DeserializationHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * RSS/ATOM Channel parser
 */
public class RssParser
{
    private XmlPullParser parser;

    private Rss rss;

    /**
     * Constructor
     * @param feedXml String feed XML markup
     * @throws XmlPullParserException
     * @throws IOException
     */
    public RssParser(String feedXml)
            throws IOException, XmlPullParserException
    {
        // init parser and do parse

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        parser = factory.newPullParser();
        parser.setInput(new StringReader(feedXml));

        rss = new Rss();
        rss.channel = new Channel();

        doParse();
    }

    /**
     * Parse XML rss feed
     * @throws IOException
     * @throws XmlPullParserException
     */
    private void doParse()
            throws IOException, XmlPullParserException
    {
        int eventState = parser.getEventType();

        rss.channel.items = new ArrayList<Item>();

        while (eventState != XmlPullParser.END_DOCUMENT)
        {
            String s = parser.getName();

            if (eventState == XmlPullParser.START_TAG && parser.getDepth() == 3)
            {
                String elemName = getElementName(parser);

                try
                {
                    if (elemName.equals("image") && parser.nextTag() == XmlPullParser.START_TAG)
                    {
                        rss.channel.image = new Image();

                        while (parser.getName() == null || ! parser.getName().equals("url"))
                        {
                           parser.next();
                        }

                        rss.channel.image.url = parser.nextText().trim();
                    }
                    else if (elemName.equals("item") && parser.nextTag() == XmlPullParser.START_TAG)
                    {
                        rss.channel.items.add(parseItem(parser));
                    }
                    else if (elemName.equals("lastBuildDate"))
                    {
                        rss.channel.lastBuildDate = DeserializationHelper
                                .parseDate(parser.getText().trim());
                    }
                    else
                    {
                        Channel.class.getField(elemName).set(rss.channel, parser.nextText().trim());
                    }
                }
                catch (Exception e)
                {
                    e.getCause();
                }
            }

            eventState = parser.next();
        }
    }

    /**
     * Parse rss item
     * @param parser XMLPullParser parser currently at item tag
     * @return ComicItem parsed item
     * @throws IOException
     * @throws XmlPullParserException
     */
    private Item parseItem(XmlPullParser parser)
            throws IOException, XmlPullParserException
    {
        Item item = new Item();

        int    eventState = parser.getEventType();
        String elemName   = getElementName(parser);

        item.categories = new ArrayList<Category>();

        // while null element or not end of feed tiem tag
        while (elemName == null || ! ((elemName.equals("item") && eventState == XmlPullParser.END_TAG)))
        {
            if (eventState == XmlPullParser.START_TAG)
            {
                try
                {
                    if (elemName.equals("enclosure"))
                    {
                        item.enclosure = new Enclosure();
                        item.enclosure.link   = parser.getAttributeValue(null, "url");
                        item.enclosure.mime   = parser.getAttributeValue(null, "type");
                        item.enclosure.length = parser.getAttributeValue(null, "length");
                    }
                    else if (elemName.equals("guid"))
                    {
                        item.guid = new Guid();
                        item.guid.value = parser.nextText().trim();
                    }
                    else if (elemName.equals("category"))
                    {
                        Category category = new Category();

                        category.value = parser.nextText().trim();

                        item.categories.add(category);
                    }
                    else if (elemName.equals("updated") || elemName.equals("pubDate"))
                    {
                        item.updated = DeserializationHelper.parseDate(parser.nextText().trim());
                    }
                    else if (elemName.equals("enclosure"))
                    {
                        item.enclosure = new Enclosure();
                        item.enclosure.link   = parser.getAttributeValue(null, "url");
                        item.enclosure.mime   = parser.getAttributeValue(null, "type");
                        item.enclosure.length = parser.getAttributeValue(null, "length");
                    }
                    else
                    {
                        Item.class.getField(elemName).set(item, parser.nextText().trim());
                    }
                }
                catch (Exception e) { }
            }

            eventState = parser.next();
            elemName   = getElementName(parser);
        }

        return item;
    }

    /**
     * Get current element name via a pull parser
     * @param parser XMLPullParser parser currently at item tag
     * @return String element name
     */
    private String getElementName(XmlPullParser parser)
    {
        String elemName = parser.getName();

        if (elemName != null && elemName.contains(":"))
        {
            elemName = elemName.substring(elemName.indexOf(':') +1);
        }

        return  elemName;
    }

    /**
     * Get parsed Rss
     * @return Rss parsed feed
     */
    public Rss getRss()
    {
        return rss;
    }
}

