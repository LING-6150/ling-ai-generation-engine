package com.ling.lingaicodegeneration.monitor;

import java.util.Locale;

/** Classifies provider failures into low-cardinality metric labels. */
public final class AiProviderErrorClassifier {

    private AiProviderErrorClassifier() {
    }

    public static String classify(Throwable error) {
        if (error == null) {
            return "unknown";
        }
        String text = collectMessages(error).toLowerCase(Locale.ROOT);
        if (containsAny(text, "handshake", "tls", "ssl", "remote host terminated")) {
            return "tls_handshake";
        }
        if (containsAny(text, "timeout", "timed out", "read timed out")) {
            return "timeout";
        }
        if (containsAny(text, "429", "too many requests", "rate limit")) {
            return "rate_limit";
        }
        if (containsAny(text, "connection reset", "connection refused", "connection aborted", "broken pipe")) {
            return "connection";
        }
        if (containsAny(text, "temporarily unavailable", "503", "502", "504", "bad gateway")) {
            return "provider_unavailable";
        }
        return "provider_error";
    }

    public static boolean isTransient(Throwable error) {
        String type = classify(error);
        return switch (type) {
            case "tls_handshake", "timeout", "rate_limit", "connection", "provider_unavailable" -> true;
            default -> false;
        };
    }

    private static boolean containsAny(String text, String... markers) {
        for (String marker : markers) {
            if (text.contains(marker)) {
                return true;
            }
        }
        return false;
    }

    private static String collectMessages(Throwable error) {
        StringBuilder builder = new StringBuilder();
        Throwable current = error;
        while (current != null) {
            if (current.getMessage() != null) {
                builder.append(current.getMessage()).append(' ');
            }
            current = current.getCause();
        }
        return builder.toString();
    }
}
