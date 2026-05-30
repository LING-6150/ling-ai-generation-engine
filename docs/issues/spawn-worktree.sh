#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <task-name>" >&2
  echo "Example: $0 engine-6-token-budget" >&2
  exit 1
fi

TASK_NAME="$1"
ROOT_DIR="$(git rev-parse --show-toplevel)"
WORKTREE_ROOT="$ROOT_DIR/.codex/worktrees"
WORKTREE_DIR="$WORKTREE_ROOT/$TASK_NAME"
BRANCH_NAME="codex/$TASK_NAME"
BASE_BRANCH="main"

mkdir -p "$WORKTREE_ROOT"

if [[ -d "$WORKTREE_DIR" ]]; then
  echo "❌ Worktree already exists: $WORKTREE_DIR" >&2
  exit 1
fi

if git show-ref --verify --quiet "refs/heads/$BRANCH_NAME"; then
  echo "❌ Branch already exists: $BRANCH_NAME" >&2
  exit 1
fi

git fetch origin "$BASE_BRANCH" >/dev/null 2>&1 || true
git worktree add -b "$BRANCH_NAME" "$WORKTREE_DIR" "origin/$BASE_BRANCH"

echo "✅ Worktree created: $WORKTREE_DIR"
echo "✅ Branch: $BRANCH_NAME"
echo "cd '$WORKTREE_DIR'"
