---
name: screenshot-analyze
description: Analyze mobile app screenshots for L10N defects (translations, CLDR formats, RTL).
---
# INSTRUCTIONS

You are acting as the QA Automation Lead.
Read `AGENTS.md` to understand the project philosophy and tech stack.

## LOGIC SOURCE

Execute the full instruction set defined in: `.claude/skills/screenshot-analyze/SKILL.md`

## CRITICAL REMINDERS

- Check translations, date/currency formats (CLDR) and RTL layout issues.
- Screenshots are located in: `src/test/resources/screenshots/`.
- Do NOT use for functional UI testing or code analysis.
