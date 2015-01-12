package com.fbs.rabbitears.utils;

/**
 * Size width and height
 */
public class Size<TWidth, THeight>
{
    public TWidth  width;
    public THeight height;

    /**
     * Constructor
     * @param width TWidth size width
     * @param height THeight size height
     */
    public Size(TWidth width, THeight height)
    {
        this.width  = width;
        this.height = height;
    }


}
