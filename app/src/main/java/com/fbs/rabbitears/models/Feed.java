package com.fbs.rabbitears.models;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.fbs.rabbitears.Config;
import com.fbs.rabbitears.R;
import com.fbs.rabbitears.tasks.DownloadFeedImageTask;

import java.util.Date;
import java.util.List;

/**
 * Feed Table Model
 */
@Table(name = "Feeds")
public class Feed extends Model
    implements Parcelable
{
    @Column(unique = true, index = true)
    public String source;
    @Column
    public String link;
    @Column
    public String image;
    @Column
    public String title;
    @Column
    public String description;

    @Column(name = "updated_at")
    public Date updatedAt;

    private Bitmap cachedImage;

    public static final Parcelable.Creator<Feed> CREATOR;

    /**
     * Constructor
     */
    public Feed()
    {
        super();
    }

    /**
     * Constructor
     * @param source String source of feed
     */
    public Feed(String source)
    {
        this();
        this.source = source;
    }

    /**
     * Constructor
     * @param in Parcel input
     */
    private Feed(Parcel in)
    {
        this();
        source = in.readString();
        link   = in.readString();
        image  = in.readString();
        title  = in.readString();

        description = in.readString();
        updatedAt   = (Date)in.readSerializable();
        cachedImage = in.readParcelable(Bitmap.class.getClassLoader());
    }

    /**
     * Static Initializer
     */
    static
    {
        CREATOR = new Parcelable.Creator<Feed>() // init parcel creator
        {
            public Feed createFromParcel(Parcel in)
            {
                return new Feed(in);
            }

            public Feed[] newArray(int size)
            {
                return new Feed[size];
            }
        };
    }

    /**
     * Load a parcelable Feed from the database if exists or use from passed bundle.
     * The Feed witll be matched by its unique source column from parcel feed.
     * @param bundle Bundle containing Feed parcel
     * @param key    String key to Feed in bundle
     * @return Feed from database or parcel
     */
    public static Feed loadParcelable(Bundle bundle, String key)
    {
        Feed feed = bundle.getParcelable(key);

        List<Feed> feedsFomDb = getWhere("source = ?", feed.source);

        if (! feedsFomDb.isEmpty())
        {
            Bitmap cached = feed.cachedImage;

            feed = feedsFomDb.get(0);

            feed.cachedImage = cached;
        }

        return feed;
    }

    /**
     * Get all Feed Items associated with Feed
     * @return List of FeedItem associated items
     */
    public List<FeedItem> getItems()
    {
        return getMany(FeedItem.class, "feed_id");
    }

    /**
     * Get all Feeds from database
     * @return List of Feed feeds from database
     */
    public static List<Feed> getAll()
    {

        return new Select()
                .all()
                .from(Feed.class)
                .execute();
    }

    /**
     * Get all Feeds from database matching a certain clause
     * @param clause String conditional where clause
     * @param args   Object... object arguments for prepared statement
     * @return List of Feed feeds matching clause
     */
    public static List<Feed> getWhere(String clause, Object... args)
    {
        return new Select()
                .from(Feed.class)
                .where(clause, args)
                .execute();
    }

    /**
     * Deletes all Feeds and reset AI ID
     */
    public static void deleteAll()
    {
        new Delete()
                .from(Feed.class)
                .execute();

        ActiveAndroid.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'Feeds'");
    }

    /**
     * Get updated_at date as formatted String
     * @return String formatted updated_at Date
     */
    public String getUpdatedAtString()
    {
        return Config.getDisplayDateFormat().format(updatedAt);
    }

    /**
     * Get cached Bitmap image of Feed
     * @return Bitmap cached downloaded feed image
     */
    public void setCachedImageView(ImageView view)
    {
        if (cachedImage == null)
        {
            String title   = Config.getResources().getString(R.string.image_download_title);
            String message = Config.getResources().getString(R.string.image_download_message);

            new DownloadFeedImageTask(this, ProgressDialog.show(view.getContext(), title, message)).execute(view);
        }
        else
        {
            view.setImageBitmap(cachedImage);
        }
    }

    /**
     * Set cached Bitmap image of Feed
     */
    public void setCachedImage(Bitmap bitmap)
    {
        cachedImage = bitmap;
    }

    public Bitmap getCachedImage()
    {
        if (cachedImage != null)
        {
            return cachedImage;
        }
        else
        {
            throw new NullPointerException("Image has not beed cached yet!");
        }
    }

    /**
     * Determine if a Feed is persisted to database
     * @return True if persisted, false if not
     * (this value is cached and will not be accurate on delete!)
     */
    public boolean isSaved()
    {
        return getId() != null;
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
        out.writeString(source);
        out.writeString(link);
        out.writeString(image);
        out.writeString(title);

        out.writeString(description);
        out.writeSerializable(updatedAt);
        out.writeParcelable(cachedImage, flag);
    }

    /**
     * Feed as string
     * @return String object summary
     */
    @Override
    public String toString()
    {
        return super.toString().replace('@', '_'); // eg. "Feed 12"
    }
}
