package com.fbs.rabbitears.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.fbs.rabbitears.Config;
import com.fbs.rabbitears.R;
import com.fbs.rabbitears.adapters.FeedArrayAdapter;
import com.fbs.rabbitears.fragments.AddFeedDialog;
import com.fbs.rabbitears.helpers.FileHelper;
import com.fbs.rabbitears.helpers.ViewHelper;
import com.fbs.rabbitears.models.Feed;
import com.fbs.rabbitears.models.FeedItem;
import com.fbs.rabbitears.models.ItemMedia;

import butterknife.InjectView;

/**
 * Feed Lister (LaunchActivity)
 *
 * Feed list selection and adding
 */
public class FeedLister extends BaseActivity
    implements AddFeedDialog.OnFeedDownloadedListener
{
    @InjectView(R.id.feed_list)
    protected ListView         feedList;

    protected FeedArrayAdapter feedAdapter;
    protected DialogInterface.OnClickListener clearConfirmListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feed_lister);

        onFirstRun();
    }

    /**
     * Setup injected view controls and clear listener
     * @param savedInstanceState Bundle state
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        feedAdapter = new FeedArrayAdapter(this);

        feedList.setAdapter(feedAdapter);

        clearConfirmListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int button)
            {
                if (button == DialogInterface.BUTTON_POSITIVE)
                {
                    Feed.deleteAll();
                    ItemMedia.deleteAll();
                    FeedItem.deleteAll();

                    FileHelper.clearImageCache();

                    onResume();
                }
            }
        };
    }

    /**
     * Reload list on dataset changed
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        feedAdapter.clear();
        feedAdapter.addAll(Feed.getAll());
        feedAdapter.sort();
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds getItems to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed_lister, menu);

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
            case R.id.action_new:

                onAddSelect();
                break;
            case R.id.action_clear:

                onClearSelect();
                break;
            case R.id.action_settings:

                onSettingsSelect();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Recives a downloaded feed from the feed download dialog
     * @param feed Feed downloaded feed
     */
    @Override
    public void onFeedReceive(Feed feed)
    {
        if (feed != null)
        {
            feed.save();
            feedAdapter.add(feed);

            onResume();

            String message = String.format(getResources().getString(R.string.feed_url_added), feed.title);

            Toast.makeText(
                    this, message, Toast.LENGTH_LONG
            ).show();
        }
        else
        {
            String message = getResources().getString(R.string.feed_url_error_read);

            Toast.makeText(
                    this, message, Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Displays first run info message
     */
    private void onFirstRun()
    {
        if (Config.isFirstRun())
        {
            String message = getResources().getString(R.string.first_message);

            ViewHelper.infoDialog(message, null, this).show();
        }
    }

    /**
     * Show add feed dialog on new select
     */
    private void onAddSelect()
    {
        AddFeedDialog addFeed = new AddFeedDialog();

        addFeed.show(getFragmentManager(), getResources().getString(R.string.feed_options_tag));
    }

    /**
     * Clear all feeds and cached feed item data on user request on clear select
     */
    private void onClearSelect()
    {
        String message = getResources().getString(R.string.clear_message);

        ViewHelper.confirmDialog(message, clearConfirmListener, this).show();
    }

    /**
     * Launch settings preference activity on settings select
     */
    private void onSettingsSelect()
    {
        Intent startSettings = new Intent(this, Settings.class);

        startSettings.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Settings.FeedItem.class.getName());
        startSettings.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);

        startActivity(startSettings);
    }
}
