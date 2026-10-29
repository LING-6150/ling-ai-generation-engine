package com.ling.lingaicodegeneration.ai.multiagent.verify;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Opens index.html (with its sibling CSS/JS) in headless Chromium and records
 * load failures, uncaught page errors, console errors and failed resource loads.
 */
@Slf4j
@Component
public class HtmlRuntimeVerifier implements ArtifactVerifier {

    static final int MAX_FAILURES = 5;
    static final int MAX_MESSAGE_CHARS = 300;
    private static final double LOAD_TIMEOUT_MS = 10_000;
    /** Let timers and deferred scripts run briefly after the load event. */
    private static final double SETTLE_MS = 500;

    @Override
    public VerificationReport verify(String outputDir) {
        long start = System.nanoTime();
        Path index = Paths.get(outputDir, "index.html");
        if (!Files.isRegularFile(index)) {
            return VerificationReport.failed(
                    List.of(new VerificationReport.Failure("missing_entry", "index.html not found")),
                    elapsedMillis(start));
        }

        List<VerificationReport.Failure> failures = Collections.synchronizedList(new ArrayList<>());
        boolean launched = false;
        // Never download browsers from inside the server: a missing browser means
        // "verifier unavailable", not a surprise multi-hundred-MB download.
        Playwright.CreateOptions options = new Playwright.CreateOptions()
                .setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1"));
        try (Playwright playwright = Playwright.create(options);
             Browser browser = playwright.chromium().launch()) {
            launched = true;
            Page page = browser.newPage();
            page.onConsoleMessage(message -> {
                if ("error".equals(message.type())) {
                    add(failures, "console_error", message.text());
                }
            });
            page.onPageError(error -> add(failures, "page_error", error));
            page.onRequestFailed(request ->
                    add(failures, "resource_failed", request.url() + " " + request.failure()));

            page.navigate(index.toUri().toString(), new Page.NavigateOptions()
                    .setWaitUntil(WaitUntilState.LOAD)
                    .setTimeout(LOAD_TIMEOUT_MS));
            page.waitForTimeout(SETTLE_MS);
        } catch (RuntimeException e) {
            if (!launched) {
                log.warn("HtmlRuntimeVerifier unavailable for {}: {}", outputDir, e.getMessage());
                return VerificationReport.unavailable(truncate(e.getMessage()), elapsedMillis(start));
            }
            add(failures, "load_failure", e.getMessage());
        }

        long elapsed = elapsedMillis(start);
        return failures.isEmpty()
                ? VerificationReport.passed(elapsed)
                : VerificationReport.failed(List.copyOf(failures), elapsed);
    }

    private static void add(List<VerificationReport.Failure> failures, String type, String message) {
        if (failures.size() < MAX_FAILURES) {
            failures.add(new VerificationReport.Failure(type, truncate(message)));
        }
    }

    private static String truncate(String message) {
        if (message == null) return "";
        return message.length() <= MAX_MESSAGE_CHARS ? message : message.substring(0, MAX_MESSAGE_CHARS) + "...";
    }

    private static long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}
