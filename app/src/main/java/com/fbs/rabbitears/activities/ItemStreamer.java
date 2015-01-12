package com.fbs.rabbitears.activities;

import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.fbs.rabbitears.Config;
import com.fbs.rabbitears.R;
import com.fbs.rabbitears.models.FeedItem;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Media Streamer
 *
 * Stream RSS media items
 */
public class ItemStreamer extends BaseActivity
{
    @InjectView(R.id.stream_play_pause)
    protected ImageButton playPauseButton;
    @InjectView(R.id.stream_seek)
    protected SeekBar trackSeekBar;
    @InjectView(R.id.stream_view)
    protected com.fbs.rabbitears.views.MediaStreamer streamer;

    private FeedItem    feedItem;

    /**
     * Load feed item and set action bar
     * @param savedInstanceState Bundle state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String itemKey = Config.getKeyValue("feed_item", null);

        feedItem    = getIntent().getExtras().getParcelable(itemKey);

        getActionBar().setTitle(feedItem.title);

        setContentView(R.layout.activity_item_streamer);
    }

    /**
     * Setup streamer and buffer stream
     * @param savedInstanceState Bundle state
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        streamer.setPlayPauseButton(playPauseButton);
        streamer.setTrackSeekBar(trackSeekBar);

        streamer.setPlayButtonImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_splay));
        streamer.setPauseButtonImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_spause));

        streamer.buffer(feedItem.itemMedia.link, feedItem.itemMedia.mime);
    }

    /**
     * Resize stream MediaStreamer on rotation change
     * @param newConfig Configuration changed config
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        streamer.adjust(newConfig);
    }

    /**
     * Release streamer resources
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        streamer.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_item_streamer, menu);
        return true;
    }

    /**
     * Destroy on stop
     */
    @OnClick(R.id.stream_stop)
    protected void onStopClick()
    {
        finish();
    }


}
