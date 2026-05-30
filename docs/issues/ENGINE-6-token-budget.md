# ENGINE-6 — Actual Token Budget Accounting

GitHub issue: https://github.com/LING-6150/ling-ai-generation-engine/issues/6

## Context

The service currently blocks daily token usage using a prompt-length estimate before generation. Actual token usage is already observed in LangChain4j callbacks and Prometheus token counters. We want a clearer accounting path that keeps a cheap pre-flight guard while relying on actual usage for quota accumulation.

## Scope

Allowed files/modules:

- `src/main/java/com/ling/lingaicodegeneration/service/impl/AppServiceImpl.java`
- `src/main/java/com/ling/lingaicodegeneration/monitor/*`
- New small helper under `src/main/java/com/ling/lingaicodegeneration/ratelimit/` or `monitor/` if needed
- Matching offline unit tests under `src/test/java/...`

Avoid:

- Real LLM calls
- Real Redis/MySQL dependency in tests
- Broad service refactors
- Frontend changes
- Git commit / push / PR

## Acceptance Criteria

- Pre-flight token check is clearly named/documented as an estimate.
- Actual token accumulation path remains based on model metadata from callbacks.
- Any extracted quota/key/accounting logic has offline unit tests.
- `./mvnw -q test` passes, or exact failure reason is reported.

## Drop-in prompt

```
You are a Codex worker for `ling-ai-generation-engine`.

Task: ENGINE-6 / Issue #6 — Actual Token Budget Accounting.

Hard rules:
1. Do not git commit.
2. Do not git push.
3. Do not create or close PRs/issues.
4. Only edit files in this issue's allowed scope.
5. Do not call real LLMs.
6. Do not require real Redis/MySQL in tests.
7. Do not invent test results.

Read first:
- ENGINE_HARDENING_NOTES.md
- src/main/java/com/ling/lingaicodegeneration/service/impl/AppServiceImpl.java
- src/main/java/com/ling/lingaicodegeneration/monitor/AiModelMonitorListener.java
- src/main/java/com/ling/lingaicodegeneration/monitor/AiModelMetricsCollector.java
- docs/issues/ENGINE-6-token-budget.md

Goal:
Improve clarity and testability of daily token budget accounting. Keep the cheap pre-flight estimate, but make it explicit that actual token usage is accumulated from LangChain4j token metadata. Extract small helper logic if useful, and add offline unit tests.

Acceptance criteria:
- Pre-flight token check is clearly named/documented as an estimate.
- Actual token accumulation path remains based on model metadata from callbacks.
- New helper/key/estimate logic is covered by offline tests where practical.
- `./mvnw -q test` passes, or you report the exact failure.

When done, output exactly:
- Summary
- Changed files
- Tests run
- Risks / caveats
- Suggested notes snippet for ENGINE_HARDENING_NOTES.md
- Suggested commit message
```
