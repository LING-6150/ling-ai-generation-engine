package com.ling.lingaicodegeneration.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;

/**
 * Cache key generator utility.
 * Converts any object to a fixed-length MD5 hash key for Redis.
 *
 * Why MD5:
 * - Ensures consistent key length (avoids Redis key-too-long issues)
 * - JSON serialization guarantees objects with same content produce same key
 * - Handles null values and complex nested objects safely
 */
public class CacheKeyUtils {

    /**
     * Generate a cache key from any object via JSON serialization + MD5 hashing.
     *
     * @param obj the object to generate a key for
     * @return fixed-length MD5 hex string
     */
    public static String generateKey(Object obj) {
        if (obj == null) {
            return DigestUtil.md5Hex("null");
        }
        // JSON serialize first to ensure objects with same content produce the same string
        String jsonStr = JSONUtil.toJsonStr(obj);
        return DigestUtil.md5Hex(jsonStr);
    }
}