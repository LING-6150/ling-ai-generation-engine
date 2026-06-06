package com.ling.lingaicodegeneration.monitor;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DailyTokenBudgetAccountingTest {

    @Test
    void dailyTokenKeyUsesStableDateFormat() {
        String key = DailyTokenBudgetAccounting.dailyTokenKey("42", LocalDate.of(2026, 5, 30));

        assertEquals("daily_token:42:20260530", key);
    }

    @Test
    void estimatePreflightTokensUsesPromptLengthEstimatePlusResponseBuffer() {
        assertEquals(500L, DailyTokenBudgetAccounting.estimatePreflightTokens(""));
        assertEquals(503L, DailyTokenBudgetAccounting.estimatePreflightTokens("abcdefghijkl"));
        assertEquals(500L, DailyTokenBudgetAccounting.estimatePreflightTokens(null));
    }

    @Test
    void wouldExceedDailyLimitRejectsOnlyWhenEstimateCrossesBudget() {
        long limit = DailyTokenBudgetAccounting.DAILY_TOKEN_LIMIT;

        assertFalse(DailyTokenBudgetAccounting.wouldExceedDailyLimit(limit - 500L, 500L));
        assertTrue(DailyTokenBudgetAccounting.wouldExceedDailyLimit(limit - 499L, 500L));
        assertTrue(DailyTokenBudgetAccounting.wouldExceedDailyLimit(limit + 1L, 0L));
    }

    @Test
    void exposesAccountingConstantsUsedByRedisCounter() {
        assertEquals(10_000_000L, DailyTokenBudgetAccounting.DAILY_TOKEN_LIMIT);
        assertEquals(Duration.ofHours(25), DailyTokenBudgetAccounting.DAILY_COUNTER_TTL);
    }
}
