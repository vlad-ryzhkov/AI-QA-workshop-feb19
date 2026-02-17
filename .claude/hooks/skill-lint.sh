#!/bin/bash
# Post-edit hook: быстрая валидация SKILL.md и qa_agent.md
# Полный аудит: /skill-audit

set -e

INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

if [ -z "$FILE_PATH" ]; then
  exit 0
fi

# Filter: only skill files and qa_agent.md
if [[ ! ("$FILE_PATH" == */.claude/skills/*/SKILL.md || "$FILE_PATH" == */.claude/qa_agent.md) ]]; then
  exit 0
fi

FINDINGS=""
FILENAME=$(basename "$FILE_PATH")
SKILL_DIR=$(basename "$(dirname "$FILE_PATH")")
LABEL="${SKILL_DIR}/${FILENAME}"

# Check 1: Line count
LINE_COUNT=$(wc -l < "$FILE_PATH" | tr -d ' ')
if [ "$LINE_COUNT" -gt 500 ]; then
  FINDINGS="${FINDINGS}\n  ⛔ CRITICAL: ${LINE_COUNT} строк (лимит: 500)"
elif [ "$LINE_COUNT" -gt 300 ]; then
  FINDINGS="${FINDINGS}\n  ⚠️ WARNING: ${LINE_COUNT} строк (рекомендация: ≤300)"
fi

# Check 2: Self-Review Protocol (the anti-pattern, not the prohibition)
if grep -q 'Формат отчёта Self-Review\|Алгоритм Self-Review\|Scorecard Self-Review' "$FILE_PATH" 2>/dev/null; then
  FINDINGS="${FINDINGS}\n  ⛔ CRITICAL: Self-Review Protocol — заменить на Post-Check inline"
fi

# Check 3: "НЕ ИСПРАВЛЯТЬ" as instruction (bold markdown = instruction, backticks = reference)
if grep -q '\*\*НЕ ИСПРАВЛЯТЬ\*\*' "$FILE_PATH" 2>/dev/null; then
  FINDINGS="${FINDINGS}\n  ⛔ CRITICAL: 'НЕ ИСПРАВЛЯТЬ' — заменить на 'ИСПРАВЬ КОД/аудит'"
fi

if [ -n "$FINDINGS" ]; then
  echo -e "🔍 skill-lint: ${LABEL}${FINDINGS}" >&2
  echo -e "  💡 Исправь найденные проблемы. Для полного аудита: /skill-audit" >&2
  exit 2
fi

exit 0
