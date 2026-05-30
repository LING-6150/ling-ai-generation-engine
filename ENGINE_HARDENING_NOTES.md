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
