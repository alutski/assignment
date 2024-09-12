# Rate limiter

Rate limiting is a technique used to control the rate at which requests are made to a server or API by limiting the number of requests over a specific period of time.

`com.assignment.ratelimiter.service.RateLimiter` is interface for rate limiters.

## Rate limiting implementation using Fixed Window algorithm.
`com.assignment.ratelimiter.service.FixedWindowRateLimiter` is a fixed window implementation.

The idea is the following:
the system counts requests in fixed time windows (e.g. one minute); 

if the number of requests exceeds the allowed limit within the current window, subsequent requests are denied.

`FixedWindowRateLimiter` is configurable: the window length and the number of requests allowed in the window can be provided via the constructor.


## Usage 
`RateLimiter` can be used inside Spring framework. To do this the Aspect and Annotation should be created.

Custom annotation to mark rate-limited endpoints:
```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
}
```

Spring's AOP to apply rate limiting to a method annotated with `@RateLimited`
```
@Aspect
@Component
public class RateLimitingAspect {

    private final RateLimiter rateLimiter;

    public RateLimitingAspect(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    // Apply the advice to any method annotated with @RateLimited
    @Before("@annotation(RateLimited) && args(request,..)")
    public void rateLimit(HttpServletRequest request) throws Exception {
        String clientId = request.getRemoteAddr(); // Get the client IP as the unique identifier

        if (!rateLimiter.isAllowed(clientId)) {
            //TODO do something e.g. throw exception
        }
    }
}
```

