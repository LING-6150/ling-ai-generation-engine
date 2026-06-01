# Engine Hardening Notes

## Operating Rules

- One scoped GitHub issue per hardening task.
- Design first, then make minimal code changes.
- Do not record LLM quality, latency, token, or pass-rate claims unless they were actually measured.
- Keep offline CI separate from real provider/manual integration tests.
- Update this file after each issue with decisions, validation commands, and caveats.

## Issues

- #1 Offline CI and test isolation
- #2 Harden CORS configuration
- #3 LLM provider retry, fallback, and metrics
- #4 Sandbox generated preview iframe

## Issue #1: Offline CI and Test Isolation

### Decision

Maven's default test path excludes tests tagged `integration`, `manual`, or `llm`. Provider-backed tests remain in the repository but are opt-in through Maven profiles.

### Commands

```bash
# Safe for CI; excludes integration/manual/LLM tests
./mvnw -q test

# Spring-context integration tests only
./mvnw -q test -Pintegration-tests

# Real provider tests only; requires provider credentials and supporting services
./mvnw -q test -Pllm-tests
```

### Notes

- GitHub Actions uses the offline command only.
- Tests that call real LLM generation are tagged `llm`.
- Broad Spring context capability checks are tagged `integration` when they are not required for offline CI confidence.

## Issue #2: Harden CORS Configuration

### Decision

Credentialed CORS now uses an explicit allowlist from `app.cors.allowed-origins` instead of wildcard origin patterns.

### Local Defaults

```yaml
app:
  cors:
    allowed-origins:
      - http://localhost:5173
      - http://127.0.0.1:5173
```

### Production Note

Set `app.cors.allowed-origins` to the deployed frontend origin only, for example the production domain or EC2 host used by the frontend. Do not use `*` with credentialed cookies.

## Issue #4: Sandbox Generated Preview iframe

### Decision

The chat and app-detail preview iframes already use `sandbox="allow-scripts allow-same-origin"`. The backend static preview endpoint now also adds defensive response headers and normalizes requested paths before serving files.

### Headers

- `X-Content-Type-Options: nosniff`
- `Referrer-Policy: no-referrer`
- `Content-Security-Policy: default-src 'self' 'unsafe-inline' 'unsafe-eval' data: blob:; frame-ancestors 'self'`

### Caveat

The CSP is intentionally permissive enough for generated demos that contain inline CSS/JS. It reduces accidental embedding and sniffing risk, but generated code is still treated as untrusted and should remain inside the sandboxed iframe.

## Issue #3: LLM Provider Retry, Fallback, and Metrics

### Decision

This pass adds low-cardinality provider error classification to Java-side metrics. It does not add automatic fallback model routing yet; fallback should be a separate bounded change once provider-specific model config is reviewed.

### Metric Change

`ai_model_errors_total` now includes `error_type`, with values such as:

- `tls_handshake`
- `timeout`
- `rate_limit`
- `connection`
- `provider_unavailable`
- `provider_error`
- `unknown`

### Eval Alignment

The `tls_handshake`, `timeout`, `rate_limit`, `connection`, and `provider_unavailable` classes should be treated as transient infrastructure errors in eval reports, not model-quality failures.

## Issue #6: Actual Token Budget Accounting

### Decision

Daily token quota now has a small shared accounting helper. The request path keeps a cheap pre-flight estimate before calling the provider, while authoritative quota accumulation remains based on LangChain4j token metadata in `AiModelMonitorListener`.

### Policy

- Pre-flight estimate: `message.length() / 4 + 500`.
- Daily limit: `100,000` tokens per user.
- Redis key: `daily_token:{userId}:{yyyyMMdd}`.
- Counter TTL: 25 hours.

### Caveat

The pre-flight estimate intentionally protects provider calls from obviously over-budget requests; actual usage may include multiple agent/model calls and is accumulated from callback metadata after responses arrive.

## Issue #7: Vue Build Result Reporting

### Decision

`VueProjectBuilder` now exposes `buildProjectWithResult`, returning `VueBuildResult` with `passed`, `durationMillis`, `outputSnippet`, and `errorSnippet`. The existing boolean `buildProject` remains as a compatibility wrapper.

### Result Shape

```java
record VueBuildResult(boolean passed, long durationMillis, String outputSnippet, String errorSnippet)
```

### Caveat

Build snippets are bounded to 4,000 characters. This creates a stable Java-side signal for future eval `VueEvaluator` integration, but does not yet persist build metadata to the database.

## Issue #8: ZIP Download Safety

### Decision

ZIP packaging now applies explicit bounds before writing entries:

- Max file count: 1,000 files.
- Max raw bytes: 50 MiB.
- Symlinks are skipped by using `Files.isRegularFile(..., NOFOLLOW_LINKS)`.
- ZIP entry names are normalized and checked before writing.

### Caveat

ZIP bytes are still buffered in memory before response write to avoid partial/corrupt responses. The raw byte limit keeps that behavior bounded for generated projects.

## Issue #9: Production Config Profiles

### Decision

Added share-safe local and production config examples plus expanded `.env.example` and README configuration notes.

### Files

- `src/main/resources/application-local.example.yml`
- `src/main/resources/application-prod.example.yml`
- `.env.example`

### Caveat

No real secrets are committed. Production deployments should provide env vars for MySQL, Redis, CORS origins, model keys, review provider, Pexels, and the agent orchestrator flag.

## Day 9B: Provider Failure Resilience Observability

### Decision

Do not add automatic fallback routing yet. The immediate reliability gap after the eval pass@3 attempts was observability: workflow-layer failures, such as an empty streaming code response, were visible in SSE but not represented as low-cardinality workflow metrics or eval retry signals.

### Changes

- `AiProviderErrorClassifier.classifyWorkflow(...)` maps workflow exceptions to stable labels:
  - provider/transient classes are preserved (`tls_handshake`, `timeout`, `rate_limit`, `connection`, `provider_unavailable`)
  - `workflow_empty_stream`
  - `workflow_empty_parse`
  - `workflow_error`
- `AiModelMetricsCollector.recordWorkflowError(...)` emits:

```text
ai_workflow_errors_total{user_id, app_id, agent_name, error_type, context_pruning}
```

- `OrchestratorAgent` records workflow error metrics when the fatal workflow catch path emits `workflow_error`.

### Eval Alignment

The eval harness now treats `CodeGenAgent produced empty code stream` and `AI returned empty code stream` as transient infra errors worth retrying. Parse-empty errors remain non-infra because they may indicate model output quality or prompt-format failure.

### Validation

```bash
./mvnw -q -Dtest=AiProviderErrorClassifierTest,AiModelMetricsCollectorTest,OrchestratorAgentTest test
./mvnw -q -DskipTests compile
git diff --check
```

### Caveat

This is not a fallback chain. It makes provider/workflow failures measurable and retryable before adding model fallback behavior that could change pruning experiment variables.
