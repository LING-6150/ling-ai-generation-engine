package com.ling.lingaicodegeneration.ratelimit.aspect;

import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.ratelimit.annotation.RateLimit;
import com.ling.lingaicodegeneration.ratelimit.enums.RateLimitType;
import com.ling.lingaicodegeneration.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * AOP aspect for @RateLimit annotation.
 *
 * Flow:
 * 1. Intercept methods annotated with @RateLimit
 * 2. Generate a Redis key based on limitType (API / USER / IP)
 * 3. Acquire a token from Redisson RRateLimiter (token bucket algorithm)
 * 4. Reject request if no token available
 *
 * Key detail: RRateLimiter uses expiry (1 hour) to prevent infinite memory growth in Redis.
 * trySetRate() only takes effect when the key doesn't exist — changing rate config requires
 * deleting the key first.
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    @Before("@annotation(rateLimit)")
    public void doBefore(JoinPoint point, RateLimit rateLimit) {
        String key = generateRateLimitKey(point, rateLimit);

        // Get or create RRateLimiter for this key
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);

        // Set expiry to prevent keys accumulating forever in Redis
        // Note: expire() is called every request — this is cheap and keeps TTL fresh
        rateLimiter.expire(java.time.Duration.ofHours(1));

        // Configure rate: rateInterval seconds window, rate tokens max
        // trySetRate() is a no-op if the key already exists (config is preserved)
        rateLimiter.trySetRate(RateType.OVERALL, rateLimit.rate(),
                rateLimit.rateInterval(), RateIntervalUnit.SECONDS);

        // Try to acquire 1 token
        boolean acquired = rateLimiter.tryAcquire(1);
        if (!acquired) {
            log.warn("Rate limit exceeded for key: {}", key);
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST, rateLimit.message());
        }
    }

    /**
     * Generate a Redis key based on rate limit type.
     * Format: "rate_limit:[customKey:]<dimension_value>"
     */
    private String generateRateLimitKey(JoinPoint point, RateLimit rateLimit) {
        StringBuilder keyBuilder = new StringBuilder("rate_limit:");

        // Append custom key prefix if provided
        if (!rateLimit.key().isEmpty()) {
            keyBuilder.append(rateLimit.key()).append(":");
        }

        switch (rateLimit.limitType()) {
            case API -> {
                // Key by class.method name
                MethodSignature signature = (MethodSignature) point.getSignature();
                Method method = signature.getMethod();
                keyBuilder.append("api:")
                        .append(method.getDeclaringClass().getSimpleName())
                        .append(".")
                        .append(method.getName());
            }
            case USER -> {
                // Key by logged-in user ID; fall back to IP if not authenticated
                try {
                    ServletRequestAttributes attributes =
                            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attributes != null) {
                        HttpServletRequest request = attributes.getRequest();
                        var loginUser = userService.getLoginUser(request);
                        keyBuilder.append("user:").append(loginUser.getId());
                    } else {
                        keyBuilder.append("ip:").append(getClientIP());
                    }
                } catch (BusinessException e) {
                    // Not logged in — fall back to IP-based limiting
                    keyBuilder.append("ip:").append(getClientIP());
                }
            }
            case IP -> keyBuilder.append("ip:").append(getClientIP());
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "Unsupported rate limit type");
        }

        return keyBuilder.toString();
    }

    /**
     * Get client IP, handling reverse proxy headers.
     * Priority: X-Forwarded-For → X-Real-IP → RemoteAddr
     */
    private String getClientIP() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For may contain multiple IPs — take the first (actual client)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "unknown";
    }
}