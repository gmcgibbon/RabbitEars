package com.fbs.rabbitears.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fbs.rabbitears.Config;
import com.fbs.rabbitears.R;
import com.fbs.rabbitears.activities.ItemLister;
import com.fbs.rabbitears.models.Feed;

import java.util.Comparator;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Feed ListView Adapter
 */
public class FeedArrayAdapter extends ArrayAdapter<Feed>
{
    protected Comparator<Feed> sorter;

    /**
     * Constructor
     * @param context Context parent activity
     */
    public FeedArrayAdapter(Context context)
    {
        super(context, R.layout.template_feed_list_item);

        sorter = new Comparator<Feed>() // sort compare by title
        {
            @Override
            public int compare(Feed first, Feed second)
            {
                return second.title.compareTo(first.title);
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
            view = inflater.inflate(R.layout.template_feed_list_item, parent, false);
            holder = new ItemHolder(view);
            view.setTag(holder);
        }
        else
        {
            holder = (ItemHolder) view.getTag();
        }

        setupItem(holder, getItem(position));

        return view;
    }

    /**
     * Sort listed feeds by title
     */
    public void sort()
    {
        sort(sorter);
    }

    /**
     * Initialize item in list
     * @param holder ItemHolder injected child views of item view
     * @param data Feed data use with injected views
     */
    private void setupItem(final ItemHolder holder, final Feed data)
    {
        data.setCachedImageView(holder.titleImage);
        holder.titleText.setText(data.title);
        holder.descriptionText.setText(data.description);
        holder.linkText.setText(data.link);

        holder.showImage(); // ensure image is shown on normal listing

        holder.titleBlock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent launchItemLister = new Intent(getContext(), ItemLister.class);

                holder.showProgress();

                launchItemLister.putExtra(Config.getKeyValue("feed"), data);

                getContext().startActivity(launchItemLister);
            }
        });
    }

    /**
     * Injection view item holder
     */
    static class ItemHolder
    {
        @InjectView(R.id.feed_image)
        protected ImageView titleImage;

        @InjectView(R.id.feed_progress)
        protected ProgressBar titleProgress;

        @InjectView(R.id.feed_title_block)
        protected View titleBlock;

        @InjectView(R.id.feed_title)
        protected TextView titleText;
        @InjectView(R.id.feed_description)
        protected TextView descriptionText;
        @InjectView(R.id.feed_link)
        protected TextView linkText;

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
