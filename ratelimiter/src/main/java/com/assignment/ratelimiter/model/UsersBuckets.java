package com.assignment.ratelimiter.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The collection of users' buckets
 */
public class UsersBuckets {

    /**
     * Map of buckets where key is a user id and value is {@link Bucket}
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public void upsert(String userId, long currentWindow) {
        buckets.putIfAbsent(userId, new Bucket(currentWindow, 0));
    }

    public Bucket getBucket(String userId) {
        return buckets.get(userId);
    }

}
