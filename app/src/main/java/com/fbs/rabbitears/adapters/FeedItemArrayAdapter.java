package com.fbs.rabbitears.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fbs.rabbitears.Config;
import com.fbs.rabbitears.R;
import com.fbs.rabbitears.activities.ItemViewer;
import com.fbs.rabbitears.models.FeedItem;

import java.util.Collection;
import java.util.Comparator;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Feed ListView Adapter
 */
public class FeedItemArrayAdapter extends ArrayAdapter<FeedItem>
{
    protected Comparator<FeedItem> sorter;

    /**
     * Constructor
     * @param context Context parent activity
     */
    public FeedItemArrayAdapter(Context context)
    {
        super(context, R.layout.template_feed_item_list_item);

        sorter = new Comparator<FeedItem>()
        {
            @Override
            public int compare(FeedItem first, FeedItem second)
            {
                return second.createdAt.compareTo(first.createdAt);
            }
        };
    }

    /**
     * Get/display item view from list
     * @param position int list index
     * @param view View item to initialize and display
     * @param parent ViewGroup parent list group
     * @return View initialized item
     */
    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        ItemHolder holder;

        if (view == null)
        {
            view = inflater.inflate(R.layout.template_feed_item_list_item, parent, false);
            holder = new ItemHolder(view);
            view.setTag(holder);
        }
        else
        {
            holder = (ItemHolder)view.getTag();
        }

        setupItem(holder, getItem(position));

        return view;
    }

    /**
     * Add all applicable feed items from a collection
     * @param collection Collection of FeedItem items to filter and add
     */
    public void filterAddAll(Collection<? extends FeedItem> collection)
    {
        filterAddAll(collection.toArray(new FeedItem[collection.size()]));
    }

    /**
     * Add app applicable feed items from a variadic array
     * @param feedItems
     */
    public void filterAddAll(FeedItem... feedItems)
    {
        for (FeedItem itemToAdd : feedItems)
        {
            boolean exists = false;

            for (int i=0; i<getCount() && ! exists; i++)
            {
                FeedItem listedItem = getItem(i);

                if (areEqual(itemToAdd, listedItem))
                {
                    if (itemToAdd.isSaved() && ! listedItem.isSaved())
                    {
                        replace(listedItem, itemToAdd);
                    }

                    exists = true;
                }
            }

            if (! exists)
            {
                add(itemToAdd);
            }
        }
    }

    /**
     * Sort items by date
     */
    public void sort()
    {
        sort(sorter);
    }

    /**
     * Replace a specified item with another replacement item
     * @param item FeedItem item to remove
     * @param replacement FeedItem item to add
     */
    public void replace(FeedItem item, FeedItem replacement)
    {
        remove(item);
        add(replacement);
        sort();
    }

    /**
     * Determines if two feed item's values are equal
     * @param first FeedItem first item to compare
     * @param second FeedItem second item to compare
     * @return True if same guiod, createdAt, or title, false if not
     */
    private boolean areEqual(FeedItem first, FeedItem second)
    {
        return  (first.guid != null && first.guid.equals(second.guid)) ||
                (first.createdAt != null && first.createdAt.equals(second.createdAt)) ||
                (first.title != null && first.title.equals(second.title));
    }

    /**
     * Initialize item in list
     * @param holder ItemHolder injected child views of item view
     * @param data FeedItem data use with injected views
     */
    private void setupItem(final ItemHolder holder, final FeedItem data)
    {
        data.feed.setCachedImageView(holder.titleImage);
        holder.titleText.setText(data.title);
        holder.dateText.setText(data.getCreatedAtString());
        holder.linkText.setText(data.link);

        if (! data.hasMedia())
        {
            holder.mediaImage.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.mediaImage.setVisibility(View.VISIBLE);
        }

        if (data.isSaved())
        {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.saveButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.deleteButton.setVisibility(View.INVISIBLE);
            holder.saveButton.setVisibility(View.VISIBLE);
        }

        holder.showImage();

        holder.deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (data.isSaved())
                {
                    final Button   save   = holder.saveButton;
                    final Button   delete = holder.deleteButton;

                    data.delete();

                    replace(data, FeedItem.copy(data));

                    save.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.GONE);
                }
            }
        });

        holder.saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (! data.isSaved())
                {
                    final Button save   = holder.saveButton;
                    final Button delete = holder.deleteButton;

                    if (data.hasMedia())
                    {
                        data.itemMedia.save(); // cache media ref
                    }

                    data.save();

                    save.setVisibility(View.GONE);
                    delete.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.titleBlock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent launchItemViewer = new Intent(getContext(), ItemViewer.class);

                holder.showProgress();

                launchItemViewer.putExtra(Config.getKeyValue("feed_item"), data);

                getContext().startActivity(launchItemViewer);
            }
        });
    }

    /**
     * Injection view item holder
     */
    static class ItemHolder
    {
        @InjectView(R.id.item_image)
        protected ImageView titleImage;
        @InjectView(R.id.item_media)
        protected ImageView mediaImage;

        @InjectView(R.id.item_progress)
        protected ProgressBar titleProgress;

        @InjectView(R.id.item_save)
        protected Button saveButton;
        @InjectView(R.id.item_delete)
        protected Button deleteButton;

        @InjectView(R.id.item_title_block)
        protected View titleBlock;

        @InjectView(R.id.item_title)
        protected TextView titleText;
        @InjectView(R.id.item_link)
        protected TextView linkText;
        @InjectView(R.id.item_date)
        protected TextView dateText;

        /**
         * Constructor
         * @param view View item to inject
         */
        public ItemHolder(View view)
        {
            ButterKnife.inject(this, view);
        }

        /**
         * Show loading progress in place of image
         */
        public void showProgress()
        {
            titleImage.setVisibility(View.INVISIBLE);
            titleProgress.setVisibility(View.VISIBLE);
        }

        /**
         * Show image in place of loading progress
         */
        public void showImage()
        {
            titleImage.setVisibility(View.VISIBLE);
            titleProgress.setVisibility(View.INVISIBLE);
        }
    }
}
