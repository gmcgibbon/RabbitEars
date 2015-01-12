package com.fbs.rabbitears.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fbs.rabbitears.R;
import com.fbs.rabbitears.RabbitEars;
import com.fbs.rabbitears.events.FeedDownloadEvent;
import com.fbs.rabbitears.helpers.ModelHelper;
import com.fbs.rabbitears.models.Feed;
import com.fbs.rabbitears.tasks.DownloadFeedTask;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Add feed dialog
 *
 * Validates and downloads a feed with a user provided url
 */
public class AddFeedDialog extends BaseDialogFragment
{
    @InjectView(R.id.feed_url_edit)
    protected EditText feedEdit;
    @InjectView(R.id.feed_url_add_progress)
    protected ProgressBar addProgress;

    protected String                   target;
    protected String                   targetXml;
    protected OnFeedDownloadedListener listener;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        listener = (OnFeedDownloadedListener)activity;
    }

    /**
     * Register for events and set dislog title
     * @param inflater LayoutInflater view inflater
     * @param container ViewGroup container to inflate
     * @param savedInstanceState Bundle state
     * @return View inflated fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_add_feed_dialog, container, false);

        EventBus.getDefault().register(this);

        getDialog().setTitle(getResources().getString(R.string.add_feed_title));

        return view;
    }

    /**
     * Paste feed url on click
     */
    @OnClick(R.id.feed_url_paste_button)
    protected void onFeedPaste()
    {
        ClipboardManager clipboard = (ClipboardManager)
                getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard.hasPrimaryClip())
        {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            if (item != null)
            {
                feedEdit.setText(item.getText());
            }
        }
    }

    /**
     * Attempt to download and add feed on click
     */
    @OnClick(R.id.feed_url_add_button)
    protected void onFeedAdd()
    {
        target = feedEdit.getText().toString().trim();

        addProgress.setVisibility(View.VISIBLE);

        new DownloadFeedTask().execute(target);
    }

    /**
     * Close dialog on click
     */
    @OnClick(R.id.feed_url_cancel_button)
    protected void onFeedCancel()
    {
        dismiss();
    }

    /**
     * Parse feed on dismiss
     * @param dialog DialogInterface dialog clsing
     */
    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);

        if (targetXml != null)
        {
            try
            {
                Feed serialized = ModelHelper.feedFromXml(target, targetXml);

                addProgress.setVisibility(View.INVISIBLE);

                listener.onFeedReceive(serialized);
            }
            catch (Exception e)
            {
                listener.onFeedReceive(null);
            }
        }
    }

    /**
     * Unregister event listening on destroy
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    /**
     * Validate feed and set downloaded xml to instance targetXml if valid
     * @param event FeedDownloadEvent async event
     */
    public void onEvent(final FeedDownloadEvent event)
    {
        final Exception exception;

        if (! event.hasException())
        {
            if (! Feed.getWhere("source = ?", target).isEmpty())
            {
                exception = new Exception(getResources().getString(R.string.feed_url_exists));
            }
            else
            {
                exception = null;

                targetXml = event.getData();
                dismiss();
            }
        }
        else
        {
            exception = event.getException();
        }

        if (exception != null)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    addProgress.setVisibility(View.INVISIBLE);

                    Toast.makeText(
                            RabbitEars.getContext(), exception.getMessage(), Toast.LENGTH_LONG
                    ).show();
                }
            });
        }
    }

    /**
     * Feed Download Event
     */
    public interface OnFeedDownloadedListener
    {
        void onFeedReceive(Feed feed);
    }
}
