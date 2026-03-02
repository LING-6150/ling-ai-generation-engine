package com.ling.lingaicodegeneration.monitor;

import lombok.extern.slf4j.Slf4j;

/**
 * ThreadLocal holder for MonitorContext.
 *
 * Why ThreadLocal?
 * The ChatModelListener callbacks (onRequest/onResponse/onError) are called
 * deep inside LangChain4j's internals. There's no easy way to pass context
 * through method parameters at that level. ThreadLocal lets us "attach"
 * context to the current thread so the listener can retrieve it.
 *
 * Important: always call clearContext() when the request is done
 * (use doFinally() in Flux) to prevent memory leaks in thread pools.
 */
@Slf4j
public class MonitorContextHolder {

    private static final ThreadLocal<MonitorContext> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * Set the monitoring context for the current thread.
     * Call this before triggering any AI model calls.
     */
    public static void setContext(MonitorContext context) {
        CONTEXT_HOLDER.set(context);
    }

    /**
     * Get the monitoring context for the current thread.
     * May return null if not set or already cleared.
     */
    public static MonitorContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * Clear the monitoring context.
     * MUST be called after the request completes (success, error, or cancel)
     * to prevent ThreadLocal memory leaks in thread pool environments.
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
}