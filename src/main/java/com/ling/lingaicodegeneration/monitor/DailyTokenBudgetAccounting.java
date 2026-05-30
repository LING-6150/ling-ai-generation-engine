package com.ling.lingaicodegeneration.monitor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Shared daily-token budget accounting rules.
 *
 * <p>The service performs a cheap pre-flight estimate before generation so an
 * obviously over-budget request can be rejected without calling the provider.
 * The authoritative quota accumulation path is still the actual token usage
 * reported by LangChain4j response metadata in {@link AiModelMonitorListener}.</p>
 */
public final class DailyTokenBudgetAccounting {

    public static final long DAILY_TOKEN_LIMIT = 100_000L;
    public static final Duration DAILY_COUNTER_TTL = Duration.ofHours(25);

    private static final int ESTIMATED_CHARS_PER_TOKEN = 4;
    private static final long ESTIMATED_RESPONSE_TOKEN_BUFFER = 500L;
    private static final DateTimeFormatter DAILY_KEY_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    private DailyTokenBudgetAccounting() {
    }

    public static String dailyTokenKey(String userId, LocalDate date) {
        return "daily_token:" + userId + ":" + DAILY_KEY_DATE_FORMAT.format(date);
    }

    public static long estimatePreflightTokens(String message) {
        int messageLength = message == null ? 0 : message.length();
        return messageLength / ESTIMATED_CHARS_PER_TOKEN + ESTIMATED_RESPONSE_TOKEN_BUFFER;
    }

    public static boolean wouldExceedDailyLimit(long alreadyUsedTokens, long estimatedRequestTokens) {
        if (estimatedRequestTokens <= 0) {
            return alreadyUsedTokens > DAILY_TOKEN_LIMIT;
        }
        return alreadyUsedTokens > DAILY_TOKEN_LIMIT - estimatedRequestTokens;
    }
}
