package com.assignment.ratelimiter.service;

import com.assignment.ratelimiter.model.Bucket;
import com.assignment.ratelimiter.model.UsersBuckets;

import java.time.Instant;

/**
 * Rate Limiting implementation with Fixed Window
 */
public class FixedWindowRateLimiter implements RateLimiter {

    private final long windowIntervalMilli;
    private final long limit;
    private final UsersBuckets usersBuckets = new UsersBuckets();

    public FixedWindowRateLimiter(long windowIntervalMilli, long limit) {
        this.windowIntervalMilli = windowIntervalMilli;
        this.limit = limit;
    }

    @Override
    public boolean isAllowed(String userId) {
        long currentWindow = Instant.now().toEpochMilli() / this.windowIntervalMilli;

        usersBuckets.upsert(userId, currentWindow);
        Bucket bucket = usersBuckets.getBucket(userId);

        synchronized (bucket) {
            if (!bucket.isInWindow(currentWindow)) {
                bucket.reset(currentWindow);
            }

            if (bucket.isLimitExceeded(this.limit)) {
                return false;
            } else {
                bucket.increaseCounter();
                return true;
            }
        }
    }
}
