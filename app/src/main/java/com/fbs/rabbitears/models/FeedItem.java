package com.fbs.rabbitears.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.fbs.rabbitears.Config;

import java.util.Date;
import java.util.List;

/**
 * Feed Item Table Model
 */
@Table(name = "FeedItems")
public class FeedItem extends Model
    implements Parcelable
{
    @Column
    public String title;
    @Column
    public String link;
    @Column
    public String description;
    @Column
    public String guid;
    @Column
    public String category;

    @Column(name = "create_at")
    public Date createdAt;

    @Column(name = "feed_id", onDelete = Column.ForeignKeyAction.CASCADE)
    public Feed feed;
    @Column(name = "item_media_id", onDelete = Column.ForeignKeyAction.CASCADE)
    public ItemMedia itemMedia;

    public static final Parcelable.Creator<FeedItem> CREATOR;

    /**
     * Constructor
     */
    public FeedItem()
    {
        super();
    }

    /**
     * Constructor
     * @param feed Feed parent feed
     */
    public FeedItem(Feed feed)
    {
        this();
        this.feed = feed;
    }

    /**
     * Constructor
     * @param in Parcel input
     */
    private FeedItem(Parcel in)
    {
        this();
        title = in.readString();
        link  = in.readString();
        description =  in.readString();
        guid        = in.readString();
        category    = in.readString();

        createdAt = (Date)in.readSerializable();
        feed      = in.readParcelable(Feed.class.getClassLoader());
        itemMedia = in.readParcelable(ItemMedia.class.getClassLoader());

    }

    /**
     * Static Initializer
     */
    static
    {
        CREATOR = new Parcelable.Creator<FeedItem>()
        {
            public FeedItem createFromParcel(Parcel in)
            {
                return new FeedItem(in);
            }

            public FeedItem[] newArray(int size)
            {
                return new FeedItem[size];
            }
        };
    }

    /**
     * Deletes all Feed Items and reset AI ID
     */
    public static void deleteAll()
    {
        new Delete()
                .from(FeedItem.class)
                .execute();

        ActiveAndroid.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'FeedItems'");
    }

    /**
     * Get all FeedItems from database matching a certain clause
     * @param clause String conditional where clause
     * @param args   Object... object arguments for prepared statement
     * @return List of FeedItem items matching clause
     */
    public static List<FeedItem> getWhere(String clause, Object... args)
    {
        return new Select()
                .from(FeedItem.class)
                .where(clause, args)
                .execute();
    }

    /**
     * Get all FeedItems from database
     * @return List of FeedItem all items
     */
    public static List<FeedItem> getAll()
    {

        return new Select()
                .all()
                .from(FeedItem.class)
                .execute();
    }

    /**
     * Copy value of a specified FeedItem
     * @param source FeedItem item to copy
     * @return FeedItem copied item
     */
    public static FeedItem copy(FeedItem source)
    {
        FeedItem copy   = new FeedItem(); // unpersisted item copy

        copy.title       = source.title;
        copy.link        = source.link;
        copy.description = source.description;
        copy.guid        = source.guid;
        copy.category    = source.category;
        copy.createdAt   = source.createdAt;
        copy.feed        = source.feed;
        copy.itemMedia   = source.itemMedia;

        return copy;
    }

    /**
     * Determine if a FeedItem is persisted to database
     * @return True if persisted, false if not
     * (this value is cached and will not be accurate on delete!)
     */
    public boolean isSaved()
    {
        return getId() != null;
    }

    /**
     * Determines if a FeedItem has enclosed media (audio, video, etc.)
     * @return True if item contains media, false if not
     */
    public boolean hasMedia()
    {
        return itemMedia != null;
    }

    /**
     * Get created at date as string
     * @return String formatted created at date
     */
    public String getCreatedAtString()
    {
        String formatted = "";

        if (createdAt != null)
        {
            formatted = Config.getDisplayDateFormat().format(createdAt);
        }

        return formatted;
    }

    /**
     * Describe parcel contents
     * @return int description
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * Write contents to parcel
     * @param out Parcel output
     * @param flag int output flags
     */
    @Override
    public void writeToParcel(Parcel out, int flag)
    {
        out.writeString(title);
        out.writeString(link);
        out.writeString(description);
        out.writeString(guid);
        out.writeString(category);

        out.writeSerializable(createdAt);
        out.writeParcelable(feed, flag);
        out.writeParcelable(itemMedia, flag);
    }
}
