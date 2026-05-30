# ENGINE-7 — Vue Build Result Reporting

GitHub issue: https://github.com/LING-6150/ling-ai-generation-engine/issues/7

## Context

The platform can generate Vue projects. Eval will become more useful if the Java side exposes a stable build result signal: passed/failed, duration, and bounded output/error snippet.

## Scope

Allowed files/modules:

- `src/main/java/com/ling/lingaicodegeneration/core/builder/VueProjectBuilder.java`
- Related builder/model/helper classes if directly needed
- Matching offline unit tests under `src/test/java/...`
- Optional small docs snippet only in final output, not `ENGINE_HARDENING_NOTES.md`

Avoid:

- Real LLM calls
- Running npm install unless already available and necessary
- Changing HTML/multi-file generation behavior
- Large workflow refactors
- Git commit / push / PR

## Acceptance Criteria

- Vue build result has a stable representation.
- Build output/error snippets are bounded.
- Duration is captured where practical.
- Existing generation paths are not disrupted.
- `./mvnw -q test` passes, or exact failure reason is reported.

## Drop-in prompt

```
You are a Codex worker for `ling-ai-generation-engine`.

Task: ENGINE-7 / Issue #7 — Vue Build Result Reporting.

Hard rules:
1. Do not git commit.
2. Do not git push.
3. Do not create or close PRs/issues.
4. Only edit files in this issue's allowed scope.
5. Do not call real LLMs.
6. Avoid running npm install; if dependencies are missing, report that instead of inventing results.
7. Do not change HTML or multi-file generation behavior unless strictly required.

Read first:
- ENGINE_HARDENING_NOTES.md
- src/main/java/com/ling/lingaicodegeneration/core/builder/VueProjectBuilder.java
- src/main/java/com/ling/lingaicodegeneration/model/enums/CodeGenTypeEnum.java
- docs/issues/ENGINE-7-vue-build-reporting.md

Goal:
Add a small, stable Vue build result representation that records passed/failed, duration, and bounded output/error snippet. Keep it minimal and testable. This is for future eval VueEvaluator integration.

Acceptance criteria:
- Vue build result has a stable representation.
- Failure/output snippets are bounded/truncated.
- Existing HTML and multi-file paths are not disrupted.
- Add offline tests where practical.
- `./mvnw -q test` passes, or you report the exact failure.

When done, output exactly:
- Summary
- Changed files
- Tests run
- Risks / caveats
- Suggested notes snippet for ENGINE_HARDENING_NOTES.md
- Suggested commit message
```
