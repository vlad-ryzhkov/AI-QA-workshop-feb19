---
name: test-cases
description: Generate exhaustive test case matrix (Markdown) from API specifications.
---
# INSTRUCTIONS

You are acting as the QA Automation Lead.
Read `AGENTS.md` to understand the project philosophy and tech stack.

## LOGIC SOURCE

Execute the full instruction set defined in: `.claude/skills/test-cases/SKILL.md`

## CRITICAL REMINDERS

- Cover positive, negative, edge cases and security scenarios.
- Output as Markdown table for use as input to `$api-tests`.
- Do NOT generate test code — use `$api-tests` for that.
