package com.ling.lingaicodegeneration.config;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PilotOriginConfigTest {
    @Test
    void differentPortOnSessionHostCannotIsolateCookies() {
        assertThrows(IllegalArgumentException.class, () -> new PilotOriginConfig(
                URI.create("http://localhost:38464"), URI.create("http://localhost:38465/api/preview"),
                URI.create("http://127.0.0.1:38465/published")));
    }
}
