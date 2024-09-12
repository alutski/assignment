package com.assignment.ratelimiter.service;

public interface RateLimiter {

    boolean isAllowed(String userId);
}
