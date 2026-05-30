# ENGINE-8 — ZIP Download Safety

GitHub issue: https://github.com/LING-6150/ling-ai-generation-engine/issues/8

## Context

Project ZIP downloads should be safe for generated projects. Large generated projects can create memory pressure, and ZIP entries must avoid unsafe path behavior.

## Scope

Allowed files/modules:

- `src/main/java/com/ling/lingaicodegeneration/service/impl/ProjectDownloadServiceImpl.java`
- `src/main/java/com/ling/lingaicodegeneration/service/ProjectDownloadService.java` only if interface change is necessary
- Matching offline unit tests under `src/test/java/...`

Avoid:

- Changing app ownership checks in `AppController`
- Changing frontend download behavior
- Broad download API redesign
- Git commit / push / PR

## Acceptance Criteria

- ZIP packaging has explicit bounded behavior such as max file count and/or max total bytes.
- Unsafe or non-normalized paths are rejected/skipped safely.
- Existing ignored names/extensions behavior is preserved.
- Normal small generated projects still package correctly.
- `./mvnw -q test` passes, or exact failure reason is reported.

## Drop-in prompt

```
You are a Codex worker for `ling-ai-generation-engine`.

Task: ENGINE-8 / Issue #8 — ZIP Download Safety.

Hard rules:
1. Do not git commit.
2. Do not git push.
3. Do not create or close PRs/issues.
4. Only edit files in this issue's allowed scope.
5. Do not change AppController ownership/permission logic unless there is a blocker.
6. Do not change frontend behavior.
7. Do not invent test results.

Read first:
- ENGINE_HARDENING_NOTES.md
- src/main/java/com/ling/lingaicodegeneration/service/impl/ProjectDownloadServiceImpl.java
- src/main/java/com/ling/lingaicodegeneration/service/ProjectDownloadService.java
- docs/issues/ENGINE-8-zip-download-safety.md

Goal:
Harden ZIP download packaging with explicit bounds and path safety while preserving normal generated-project downloads. Add offline tests for allowed/ignored/limit behavior where practical.

Acceptance criteria:
- ZIP packaging has explicit max file count and/or max total bytes.
- Unsafe paths are rejected/skipped safely.
- Existing ignored names/extensions behavior is preserved.
- Normal generated project files still package correctly.
- `./mvnw -q test` passes, or you report the exact failure.

When done, output exactly:
- Summary
- Changed files
- Tests run
- Risks / caveats
- Suggested notes snippet for ENGINE_HARDENING_NOTES.md
- Suggested commit message
```
