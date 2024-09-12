package com.assignment.ratelimiter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FixedWindowRateLimiterTest {

    private static final int LIMIT10 = 10;
    private static final int LIMIT20 = 20;
    private RateLimiter rateLimiter;

    @BeforeEach
    public void setUp() {
        rateLimiter = new FixedWindowRateLimiter(1000, LIMIT10);
    }

    @Test
    public void test10RequestsWithinWindowAllowed() {
        String userId = "user1";
        for (int i = 0; i < LIMIT10; i++) {
            assertTrue(rateLimiter.isAllowed(userId), "Request " + (i + 1) + " should be allowed");
        }
    }

    @Test
    public void test20RequestsWithinWindow10Allowed10Failed() {
        String userId = "user2";
        for (int i = 0; i < LIMIT10; i++) {
            assertTrue(rateLimiter.isAllowed(userId), "Request " + (i + 1) + " should be allowed");
        }
        for (int i = LIMIT10; i < LIMIT20; i++) {
            assertFalse(rateLimiter.isAllowed(userId), "Request " + (i + 1) + " should be denied");
        }
    }

    @Test
    public void testFastAndSlowUserConcurrently() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
            String fastUser = "fastUser";
            String slowUser = "slowUser";

            // Lists to collect results
        List<Boolean> fastUserResults = new ArrayList<>();
            List<Boolean> slowUserResults = new ArrayList<>();

            executorService.submit(() -> {
                for (int i = 0; i < LIMIT20; i++) {
                    fastUserResults.add(rateLimiter.isAllowed(fastUser));
                }
            });

            executorService.submit(() -> {
                for (int i = 0; i < LIMIT10; i++) {
                    slowUserResults.add(rateLimiter.isAllowed(slowUser));
                    try {
                        Thread.sleep(100); // Simulate slower request pattern
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS); // Wait for all tasks to complete


        for (int i = 0; i < LIMIT10; i++) {
            assertTrue(fastUserResults.get(i), "Fast user request " + (i + 1) + " should be allowed");
        }
        for (int i = LIMIT10; i < LIMIT20; i++) {
            assertFalse(fastUserResults.get(i), "Fast user request " + (i + 1) + " should be denied");
        }

        // Now, assert the results for slow user
        for (int i = 0; i < LIMIT10; i++) {
            assertTrue(slowUserResults.get(i), "Slow user request " + (i + 1) + " should be allowed");
        }
    }

}
