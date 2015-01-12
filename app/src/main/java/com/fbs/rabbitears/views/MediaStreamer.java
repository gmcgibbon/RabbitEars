package com.fbs.rabbitears.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

/**
 * Media Streamer
 *
 * A surface view for streaming media over the network
 */
public class MediaStreamer extends SurfaceView
    implements SurfaceHolder.Callback,
               SeekBar.OnSeekBarChangeListener,
               Button.OnClickListener
{
    private MediaPlayer player;
    private Handler     updateHandler;
    private Runnable    seekUpdater;

    private String      sourceAddress;
    private String      mimeType;

    private Bitmap    playButtonImage;
    private Bitmap    pauseButtonImage;

    private SeekBar     trackSeekBar;
    private ImageButton playPauseButton;

    // ---------- Initializers ----------

    /**
     * Constructor
     * @param context Context parent
     */
    public MediaStreamer(Context context)
    {
        super(context);
        init(null, 0);
    }

    /**
     * Constructor
     * @param context Context parent
     * @param attrs AttributeSet initial attributes
     */
    public MediaStreamer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * Constructor
     * @param context Context parent
     * @param attrs AttributeSet initial attributes
     * @param defStyle int style
     */
    public MediaStreamer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Initialize view
     * @param attrs AttributeSet initial attributes
     * @param defStyle int style
     */
    private void init(AttributeSet attrs, int defStyle)
    {
        // setup initial attributes

        if (! isInEditMode()) // true if view is being previewed
        {
            // setup internal componenets

            updateHandler = new Handler();
            player = new MediaPlayer();
            seekUpdater = new Runnable()
            {
                @Override
                public void run()
                {

                    if (player != null && player.isPlaying())
                    {
                        int currentPosition = player.getCurrentPosition();

                        trackSeekBar.setProgress(currentPosition);
                        updateHandler.postDelayed(this, 1000);
                    }
                }
            };
        }
    }

    // ---------- End Initializers ----------

    /**
     * Buffer media streamer
     * @param link String link source to stream
     * @param mime String mime type for special processing
     */
    public void buffer(String link, String mime)
    {
        this.sourceAddress = link;
        this.mimeType      = mime;

        this.getHolder().addCallback(this);
    }

    /**
     * Release streamer resources
     */
    public void release()
    {
        player.stop();
        player.release();

        updateHandler.removeCallbacks(seekUpdater);
    }

    /**
     * Adjust stream player scaled aspect ratio using a specified configuration
     * @param config Configuration new config to adjust to
     */
    public void adjust(Configuration config)
    {
        int surfaceWidth;
        int playerWidth  = player.getVideoWidth();
        int playerHeight = player.getVideoHeight();

        ViewGroup.LayoutParams params = getLayoutParams();

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            surfaceWidth = getResources().getDisplayMetrics().heightPixels;
        }
        else
        {
            surfaceWidth = getResources().getDisplayMetrics().widthPixels;
        }

        params.height = (int) (((float)playerHeight / (float)playerWidth) * (float)surfaceWidth);
        params.width  = surfaceWidth;

        setLayoutParams(params);
    }

    /**
     * Set tracking seek bar
     * @param seekBar SeekBar to set as tracking seek bar
     */
    public void setTrackSeekBar(SeekBar seekBar)
    {
        this.trackSeekBar = seekBar;
        this.trackSeekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * Set play pause button
     * @param button ImageButton to set as play/pause toggle button
     */
    public void setPlayPauseButton(ImageButton button)
    {
        this.playPauseButton = button;
        this.playPauseButton.setOnClickListener(this);
    }

    /**
     * Set play image
     * @param image Bitmap image to set on play/pause button in play mode
     */
    public void setPlayButtonImage(Bitmap image)
    {
        this.playButtonImage = image;
    }

    /**
     * Set pause image
     * @param image Bitmap image to set on play/pause button in pause mode
     */
    public void setPauseButtonImage(Bitmap image)
    {
        this.pauseButtonImage = image;
    }

    // ---------- Listeners ----------

    /**
     * Prepare media player on initial creation
     * @param surfaceHolder SurfaceHolder created surface
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        try
        {
            if (! player.isPlaying())
            {
                player.setDataSource(sourceAddress);
                player.prepare();

                adjust(getResources().getConfiguration());

                trackSeekBar.setMax(player.getDuration());
                updateHandler.post(seekUpdater);

                onClick(playPauseButton); // play it on prepare
            }
        }
        catch (Exception e) { }
    }

    /**
     * Reset media display on change
     * @param surfaceHolder SurfaceHolder changed surface
     * @param format int change format
     * @param width int changed width
     * @param height int changed height
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height)
    {
        player.setDisplay(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        // unused
    }

    /**
     * Toggle play/pause on click
     * @param view View button clicked
     */
    @Override
    public void onClick(View view)
    {
        if (view.equals(playPauseButton))
        {
            if (player.isPlaying())
            {
                playPauseButton.setImageBitmap(playButtonImage);
                player.pause();
                updateHandler.removeCallbacks(seekUpdater);
            }
            else
            {
                playPauseButton.setImageBitmap(pauseButtonImage);
                player.start();
                updateHandler.post(seekUpdater);
            }
        }
    }

    /**
     * Seek to position in media player on seek change from user
     * @param seekBar SeekBar changed
     * @param progress int changed progress
     * @param fromUser True if event was triggered from user interaction, false if not
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (player != null && fromUser)
        {
            player.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        // unused
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        // unused
    }

    // ---------- End Listeners ----------
}
