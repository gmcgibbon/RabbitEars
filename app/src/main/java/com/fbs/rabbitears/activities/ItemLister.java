package com.fbs.rabbitears.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.fbs.rabbitears.Config;
import com.fbs.rabbitears.R;
import com.fbs.rabbitears.adapters.FeedItemArrayAdapter;
import com.fbs.rabbitears.events.FeedDownloadEvent;
import com.fbs.rabbitears.events.ItemProcessEvent;
import com.fbs.rabbitears.helpers.ViewHelper;
import com.fbs.rabbitears.models.Feed;
import com.fbs.rabbitears.models.FeedItem;
import com.fbs.rabbitears.tasks.DownloadFeedTask;
import com.fbs.rabbitears.tasks.ProcessFeedItemsTask;

import java.util.List;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Item Lister
 *
 * Feed item list selection and caching
 */
public class ItemLister extends BaseActivity
{
    @InjectView(R.id.item_list)
    protected ListView itemList;

    protected FeedItemArrayAdapter itemAdapter;
    protected Feed                 feed;
    protected DialogInterface.OnClickListener removeConfirmListener;

    /**
     * Load parcel feed and
     * @param savedInstanceState Bundle state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String feedKey = Config.getKeyValue("feed", null);

        feed = Feed.loadParcelable(getIntent().getExtras(), feedKey);

        getActionBar().setTitle(feed.title);

        setContentView(R.layout.activity_item_lister);
    }

    /**
     * Setup injected view controls and remove listener
     * @param savedInstanceState Bundle state
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        itemAdapter = new FeedItemArrayAdapter(this);

        itemList.setAdapter(itemAdapter);

        removeConfirmListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int button)
            {
                if (button == DialogInterface.BUTTON_POSITIVE && feed.isSaved())
                {
                    feed.delete();

                    finish();
                }
            }
        };

        EventBus.getDefault().register(this);
    }

    /**
     * Reload and redownload items on resume
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        new DownloadFeedTask().execute(feed.source);
    }

    /**
     * Trigger item processing on download
     * @param event FeedDownloadEvent downloaded event data/xml
     */
    public void onEvent(final FeedDownloadEvent event)
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (event.hasData())
                {
                    new ProcessFeedItemsTask(feed).execute(event.getData());
                }
                else if (event.hasException())
                {
                    Toast.makeText(
                            ItemLister.this, event.getException().getMessage(), Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }

    /**
     * Add items to feed item list
     * @param event ItemProcessEvent downloaded event data/items
     */
    public void onEvent(final ItemProcessEvent event)
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (event.hasData())
                {
                    addItems(event.getData());
                }

                if (event.hasException())
                {
                    Toast.makeText(
                            ItemLister.this, event.getException().getMessage(), Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }

    /**
     * Unregister event bus on destroy
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds getItems to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_lister, menu);
        return true;
    }

    /**
     * Menu bar selection
     * @param item MenuItem selected item
     * @return True
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_remove:

                onRemoveSelect();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Display dialog to confirm removal of feed and remove on user request
     */
    private void onRemoveSelect()
    {
        String message = new StringBuilder(getResources().getString(R.string.action_remove))
                .append(" ")
                .append(feed.title)
                .append("?")
                .toString();

        ViewHelper.confirmDialog(message, removeConfirmListener, this).show();
    }

    /**
     * Add new items to feed item list
     * @param items List of FeedItem items to add to item list
     */
    private void addItems(List<FeedItem> items)
    {
        itemAdapter.filterAddAll(items);
        itemAdapter.sort();
        itemAdapter.notifyDataSetChanged();
    }
}
