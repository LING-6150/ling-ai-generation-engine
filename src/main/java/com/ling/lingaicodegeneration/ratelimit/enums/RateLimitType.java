package com.ling.lingaicodegeneration.ratelimit.enums;



/**
 * Rate limit dimension types.
 * - API: limits by method name (per interface)
 * - USER: limits by logged-in user ID
 * - IP: limits by client IP address (fallback for unauthenticated requests)
 */
public enum RateLimitType {
    /** Rate limit per API method */
    API,
    /** Rate limit per user ID */
    USER,
    /** Rate limit per client IP */
    IP
}