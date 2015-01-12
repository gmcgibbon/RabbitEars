package com.fbs.rabbitears.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

import java.text.DecimalFormat;

/**
 * Item Media Table
 */
@Table(name = "ItemMedia")
public class ItemMedia extends Model
    implements Parcelable
{

    @Column
    public String link;
    @Column
    public String mime;
    @Column
    public Long length;

    public static final Parcelable.Creator<ItemMedia> CREATOR;

    /**
     * Constructor
     */
    public ItemMedia()
    {
        super();
    }

    /**
     * Constructor
     * @param in Parcel input
     */
    private ItemMedia(Parcel in)
    {
        this();

        link   = in.readString();
        mime   = in.readString();
        length = in.readLong();
    }

    /**
     * Static Initializer
     */
    static
    {
        CREATOR = new Parcelable.Creator<ItemMedia>()
        {
            public ItemMedia createFromParcel(Parcel in)
            {
                return new ItemMedia(in);
            }

            public ItemMedia[] newArray(int size)
            {
                return new ItemMedia[size];
            }
        };
    }


    /**
     * Deletes all Item Media and reset AI ID
     */
    public static void deleteAll()
    {
        new Delete()
                .from(ItemMedia.class)
                .execute();

        ActiveAndroid.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'ItemMedia'");
    }

    /**
     * Get length/size in bytes as megabytes string
     * @return String formatted length
     */
    public String getLengthAsMegabytes()
    {
        String format = "";

        if (length != -1)
        {
            format = new DecimalFormat("#.##")
                    .format((length / 1024f) / 1024f) + "MB";
        }

        return format;
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
        out.writeString(link);
        out.writeString(mime);
        out.writeLong(length);
    }
}
