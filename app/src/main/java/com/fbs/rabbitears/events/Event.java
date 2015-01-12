package com.fbs.rabbitears.events;

/**
 * Generic EventBus Event for async data transmission
 */
public abstract class Event<TData>
{
    protected TData data;
    protected Exception exception;

    /**
     * Constructor
     * @param data TData event data
     */
    public Event(TData data)
    {
        this.data = data;
    }

    /**
     * Constructor
     * @param exception Exception event exception
     */
    public Event(Exception exception)
    {
        this.exception = exception;
    }

    /**
     * Constructor
     * @param data TData event data
     * @param exception Exception event exception
     */
    public Event(TData data, Exception exception)
    {
        this.data      = data;
        this.exception = exception;
    }

    /**
     * Determines if an event has data
     * @return True if data is not null, false if not
     */
    public boolean hasData()
    {
        return data != null;
    }

    /**
     * Determines if an event has an exception
     * @return True if exception is not null, false if not
     */
    public boolean hasException()
    {
        return exception != null;
    }

    /**
     * Get event data
     * @return TData event data
     */
    public TData getData()
    {
        return data;
    }

    /**
     * Get event exception
     * @return Exception event exception
     */
    public Exception getException()
    {
        return exception;
    }
}
