package com.assignment.ratelimiter.model;

/**
 * Information about one bucket: to which time window it belongs and how many requests were processed in this window.
 * Provide operations to manage a bucket inside a window
 */
public class Bucket {
    private long window;
    private int requestCount;

    public Bucket(long window, int requestCount) {
        this.window = window;
        this.requestCount = requestCount;
    }

    public boolean isInWindow(long currentWindow) {
        return window == currentWindow;
    }

    public void reset(long window) {
        this.window = window;
        this.requestCount = 0;
    }

    public boolean isLimitExceeded(long limit) {
        return this.requestCount >= limit;
    }

    public void increaseCounter() {
        this.requestCount++;
    }
}
