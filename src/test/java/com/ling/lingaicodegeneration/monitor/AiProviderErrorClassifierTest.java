package com.ling.lingaicodegeneration.monitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiProviderErrorClassifierTest {

    @Test
    void classifyDetectsTlsHandshakeErrors() {
        RuntimeException error = new RuntimeException(
                "I/O error on POST request: Remote host terminated the handshake");

        assertEquals("tls_handshake", AiProviderErrorClassifier.classify(error));
        assertTrue(AiProviderErrorClassifier.isTransient(error));
    }

    @Test
    void classifyDetectsRateLimitErrors() {
        RuntimeException error = new RuntimeException("429 Too Many Requests from provider");

        assertEquals("rate_limit", AiProviderErrorClassifier.classify(error));
        assertTrue(AiProviderErrorClassifier.isTransient(error));
    }

    @Test
    void classifyFallsBackToProviderError() {
        RuntimeException error = new RuntimeException("model returned invalid JSON");

        assertEquals("provider_error", AiProviderErrorClassifier.classify(error));
        assertFalse(AiProviderErrorClassifier.isTransient(error));
    }
}
