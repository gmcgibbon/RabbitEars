package com.fbs.rabbitears.helpers;

import com.fbs.rabbitears.Config;
import com.fbs.rabbitears.R;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * XML Deserialization Helpers
 */
public class DeserializationHelper
{
    private static final FeedParserCreator CREATOR;
    private static final GsonXml          GSON_XML;
    private static final DateDeserializer DATE_DESERIALIZER;

    /**
     * Static Initializers
     */
    static
    {
        DATE_DESERIALIZER = new DateDeserializer();
        CREATOR  = new FeedParserCreator();
        GSON_XML = new GsonXmlBuilder()
                .wrap(
                        new GsonBuilder()
                            .registerTypeAdapter(Date.class, DATE_DESERIALIZER)
                )
                .setSameNameLists(true)
                .setXmlParserCreator(CREATOR)
                .create();
    }

    /**
     * Get GSON XML deserializer
     * @return GsonXml for feed deserialization
     */
    public static GsonXml getGsonXml()
    {
        return GSON_XML;
    }

    /**
     * Parses date using a list of potential formats (see strings.xml)
     * @param str String date text
     * @return Date parsed from string or null
     */
    public static Date parseDate(String str)
    {
        String[] formats = Config.getResources().getStringArray(R.array.markup_date_formats);
        Date     date    = null;

        for (String format : formats)
        {
            try
            {
                date = new SimpleDateFormat(format)
                        .parse(str);
                break;
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }

        return date;
    }

    /**
     * Feed deserialization parser creator
     */
    static class FeedParserCreator
            implements XmlParserCreator
    {
        @Override
        public XmlPullParser createParser()
        {
            try
            {
                return XmlPullParserFactory.newInstance().newPullParser();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Custom date deserializer
     */
    static class DateDeserializer
            implements JsonDeserializer<Date>
    {
        @Override
        public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                throws JsonParseException
        {
            return parseDate(json.getAsString());
        }
    }
}
