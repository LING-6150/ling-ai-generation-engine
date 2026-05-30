# Parallel Codex Task Pack

This folder contains scoped worker tasks for running multiple Codex terminals in parallel.

## Rules

- One terminal per issue.
- Use `docs/issues/spawn-worktree.sh` from the main repo to create an isolated worktree.
- Workers must not commit, push, close issues, or create PRs.
- Workers should only edit files allowed by the issue scope.
- Workers should run offline tests and report exact results.
- The integrator will review all worktrees, merge changes, update notes, commit, push, and open one PR.

## Current Batch

- `ENGINE-6-token-budget.md` → Issue #6
- `ENGINE-7-vue-build-reporting.md` → Issue #7
- `ENGINE-8-zip-download-safety.md` → Issue #8
- `ENGINE-9-prod-config-profiles.md` → Issue #9
