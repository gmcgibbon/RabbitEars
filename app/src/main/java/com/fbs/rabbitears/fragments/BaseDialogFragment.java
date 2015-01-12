package com.fbs.rabbitears.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Base dialog fragment
 *
 * Derive to inherit common functionality
 */
public abstract class BaseDialogFragment extends DialogFragment
{
    /**
     * Inject views on created
     * @param savedInstanceState Bundle state
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
    }
}
