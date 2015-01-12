package com.fbs.rabbitears.activities;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;

/**
 * Base Activity
 *
 * Derive to inherit common functionality
 */
public abstract class BaseActivity extends Activity
{

    /**
     * Inject views on post create
     * @param savedInstanceState Bundle state
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        ButterKnife.inject(this);
    }
}
