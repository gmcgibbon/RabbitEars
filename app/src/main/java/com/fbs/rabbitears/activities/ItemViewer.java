package com.fbs.rabbitears.activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fbs.rabbitears.Config;
import com.fbs.rabbitears.R;
import com.fbs.rabbitears.helpers.ViewHelper;
import com.fbs.rabbitears.models.FeedItem;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Item Viewer
 *
 * Feed list item viewer
 */
public class ItemViewer extends BaseActivity
{
    @InjectView(R.id.item_image)
    protected ImageView itemImage;

    @InjectView(R.id.item_date)
    protected TextView dateText;
    @InjectView(R.id.media_mime)
    protected TextView mimeText;
    @InjectView(R.id.media_size)
    protected TextView sizeText;

    @InjectView(R.id.media_block)
    protected View mediaBlock;

    @InjectView(R.id.item_description)
    protected WebView itemDescription;

    protected FeedItem feedItem;

    /**
     * Load feed item and set actiona bar text
     * @param savedInstanceState Bundle state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String itemKey = Config.getKeyValue("feed_item", null);

        feedItem = getIntent().getExtras().getParcelable(itemKey);

        getActionBar().setTitle(feedItem.title);

        setContentView(R.layout.activity_item_viewer);
    }

    /**
     * Setup injected view controls
     * @param savedInstanceState Bundle state
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        feedItem.feed.setCachedImageView(itemImage);
        dateText.setText(feedItem.getCreatedAtString());

        initDescriptionWebView(feedItem.description);

        if (! feedItem.hasMedia())
        {
            mediaBlock.setVisibility(View.GONE);
        }
        else
        {
            mimeText.setText(feedItem.itemMedia.mime);
            sizeText.setText(feedItem.itemMedia.getLengthAsMegabytes());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds getItems to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_viewer, menu);
        return true;
    }

    /**
     * Start media streamer on click
     */
    @OnClick(R.id.media_stream)
    protected void onStreamClick()
    {
        if (feedItem.hasMedia())
        {
            Intent startStreamer = new Intent(this, ItemStreamer.class);

            startStreamer.putExtra(Config.getKeyValue("feed_item"), feedItem);

            startActivity(startStreamer);
        }
    }

    /**
     * Start download on click
     */
    @OnClick(R.id.media_download)
    protected void onDownloadClick()
    {
        if (feedItem.hasMedia())
        {
            try
            {
                // queue download via system download manager

                queueDownload(feedItem.itemMedia.link);

                Toast.makeText(
                        this, getResources().getString(R.string.media_queued), Toast.LENGTH_LONG
                ).show();
            }
            catch (Exception e)
            {
                Toast.makeText(
                        this, e.getMessage(), Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    /**
     * Queue download using Android's download manager
     * @param location String location of resource
     */
    private void queueDownload(String location)
    {
        Uri    netUri  = Uri.parse(location);
        String netName = new File(netUri.toString()).getName();

        DownloadManager.Request request = new DownloadManager.Request(netUri);

        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, netName
        );
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager manager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);

        manager.enqueue(request);
    }

    /**
     * Initialize description web view using preferences
     * @param content String deserialized HTML content
     */
    private void initDescriptionWebView(String content)
    {
        if (Config.getPreferences().getBoolean(getResources().getString(R.string.pref_item_scale_key), false))
        {
            content = content + "<style>* { width: 98%; }</style>";
        }

        itemDescription.loadData(
                ViewHelper.encaseHtmlDoc(content), "text/html; charset=UTF-8", null
        );
        itemDescription.getSettings().setJavaScriptEnabled(
                Config.getPreferences().getBoolean(getResources().getString(R.string.pref_item_jscript_key), true)
        );
        itemDescription.getSettings().setDomStorageEnabled(
                Config.getPreferences().getBoolean(getResources().getString(R.string.pref_item_storage_key), true)
        );
        itemDescription.getSettings().setBuiltInZoomControls(
                Config.getPreferences().getBoolean(getResources().getString(R.string.pref_item_zoom_key), false)
        );

        //itemDescription.setInitialScale(100);
        itemDescription.setBackgroundColor(Color.TRANSPARENT);
    }
}
