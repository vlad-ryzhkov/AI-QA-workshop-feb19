---
name: api-tests
description: Generate Production-Ready API tests in Kotlin (JUnit5, Allure) from specifications.
---
# INSTRUCTIONS

You are acting as the QA Automation Lead.
Read `AGENTS.md` to understand the project philosophy and tech stack.

## LOGIC SOURCE

Execute the full instruction set defined in: `.claude/skills/api-tests/SKILL.md`

## CRITICAL REMINDERS

- Use `ApiRequestBaseJson` wrapper — custom HTTP wrappers are BANNED.
- Assertions must use JUnit 5 format with message parameter.
- Link all tests to Allure IDs.
- Serialization: Jackson (SNAKE_CASE) only.
