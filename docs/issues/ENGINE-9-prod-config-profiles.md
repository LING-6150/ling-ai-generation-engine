# ENGINE-9 — Production Config Profiles

GitHub issue: https://github.com/LING-6150/ling-ai-generation-engine/issues/9

## Context

The repo has safer defaults now, but local/prod configuration should be clearer and safe to share publicly. Developers should know which environment variables are required without reading all config classes.

## Scope

Allowed files/modules:

- `src/main/resources/application.yml`
- New example config docs/files under `src/main/resources/` or `docs/`
- `README.md` only for a small config section if necessary
- `ENGINE_HARDENING_NOTES.md` should not be edited by worker; provide snippet in final output

Avoid:

- Committing secrets
- Changing production behavior without documentation
- Java code changes unless strictly necessary
- Git commit / push / PR

## Acceptance Criteria

- Local/prod configuration strategy is documented.
- Required env vars/config values are clear: DB, Redis, CORS, model keys, review provider, agent flag.
- No secrets are committed.
- Existing local defaults remain usable.
- `./mvnw -q test` passes if Java/config changes require it, or exact reason is reported.

## Drop-in prompt

```
You are a Codex worker for `ling-ai-generation-engine`.

Task: ENGINE-9 / Issue #9 — Production Config Profiles.

Hard rules:
1. Do not git commit.
2. Do not git push.
3. Do not create or close PRs/issues.
4. Only edit files in this issue's allowed scope.
5. Do not commit secrets or real API keys.
6. Prefer docs/example config over runtime behavior changes.
7. Do not invent test results.

Read first:
- ENGINE_HARDENING_NOTES.md
- src/main/resources/application.yml
- .env.example
- README.md configuration/getting-started sections
- docs/issues/ENGINE-9-prod-config-profiles.md

Goal:
Add clear local/prod configuration templates or documentation that explain environment variables for DB, Redis, CORS origins, model keys, review provider, and the agent orchestrator flag. Keep local defaults usable and do not commit secrets.

Acceptance criteria:
- Local/prod config strategy is documented.
- Required env vars/config values are clear.
- No secrets are committed.
- Existing local defaults remain usable.
- Run `./mvnw -q test` if config changes warrant it, or explain why not.

When done, output exactly:
- Summary
- Changed files
- Tests run
- Risks / caveats
- Suggested notes snippet for ENGINE_HARDENING_NOTES.md
- Suggested commit message
```
