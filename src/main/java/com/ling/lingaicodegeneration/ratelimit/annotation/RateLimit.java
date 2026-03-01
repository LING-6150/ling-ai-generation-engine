package com.ling.lingaicodegeneration.ratelimit.annotation;


import com.ling.lingaicodegeneration.ratelimit.enums.RateLimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rate limit annotation.
 * Apply to controller methods to enable distributed rate limiting via Redisson RRateLimiter.
 *
 * Example:
 *   @RateLimit(limitType = RateLimitType.USER, rate = 5, rateInterval = 60,
 *              message = "AI requests are limited to 5 per minute, please try again later")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /** Custom key prefix (optional, defaults to empty — key is auto-generated) */
    String key() default "";

    /** Max requests per time window */
    int rate() default 10;

    /** Time window in seconds */
    int rateInterval() default 1;

    /** Rate limit dimension */
    RateLimitType limitType() default RateLimitType.USER;

    /** User-facing message when rate limit is exceeded */
    String message() default "Request rate limit exceeded, please try again later";
}