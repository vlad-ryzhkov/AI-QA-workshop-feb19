#!/usr/bin/env bash
# Генерирует шаблонную структуру директории для нового skill.
# Использование: bash init_skill.sh <skill-name> [--scripts] [--references] [--assets]

set -euo pipefail

SKILLS_DIR=".claude/skills"

# --- Validation ---

if [[ $# -lt 1 ]]; then
  echo "Использование: bash $0 <skill-name> [--scripts] [--references] [--assets]"
  echo ""
  echo "Примеры:"
  echo "  bash $0 my-skill"
  echo "  bash $0 my-skill --scripts --references"
  exit 1
fi

SKILL_NAME="$1"
shift

# Validate kebab-case
if [[ ! "$SKILL_NAME" =~ ^[a-z][a-z0-9-]*$ ]]; then
  echo "❌ Ошибка: имя skill должно быть в kebab-case (только a-z, 0-9, -)"
  echo "   Получено: '$SKILL_NAME'"
  echo "   Пример: 'my-skill', 'api-tests', 'lint-check'"
  exit 1
fi

# Reject reserved names
if [[ "$SKILL_NAME" == *claude* || "$SKILL_NAME" == *anthropic* ]]; then
  echo "❌ Ошибка: имена с 'claude' или 'anthropic' зарезервированы"
  exit 1
fi

SKILL_DIR="$SKILLS_DIR/$SKILL_NAME"

# Check if already exists
if [[ -d "$SKILL_DIR" ]]; then
  echo "❌ Ошибка: директория '$SKILL_DIR' уже существует"
  exit 1
fi

# --- Parse flags ---

CREATE_SCRIPTS=false
CREATE_REFERENCES=false
CREATE_ASSETS=false

for arg in "$@"; do
  case "$arg" in
    --scripts)    CREATE_SCRIPTS=true ;;
    --references) CREATE_REFERENCES=true ;;
    --assets)     CREATE_ASSETS=true ;;
    *)
      echo "❌ Неизвестный флаг: $arg"
      echo "   Допустимые: --scripts, --references, --assets"
      exit 1
      ;;
  esac
done

# --- Create structure ---

echo "📁 Создаю skill: $SKILL_NAME"

mkdir -p "$SKILL_DIR"

# SKILL.md template
cat > "$SKILL_DIR/SKILL.md" << 'TEMPLATE'
---
name: SKILL_NAME_PLACEHOLDER
description: TODO — заполни по формуле: [Что делает]. [Когда использовать]. Не используй для [анти-примеры].
---

# /SKILL_NAME_PLACEHOLDER — TODO Название

<purpose>
TODO: 1-2 предложения — что делает и для кого.
</purpose>

## Когда использовать

- TODO: триггер 1
- TODO: триггер 2

## Входные данные

- TODO: что нужно от пользователя

## Алгоритм выполнения

### Шаг 1: TODO
TODO: конкретные действия

### Шаг 2: TODO
TODO: конкретные действия

## Формат вывода

TODO: шаблон результата

## Quality Gates

- [ ] TODO: проверка 1
- [ ] TODO: проверка 2
TEMPLATE

# Replace placeholder with actual name
sed -i '' "s/SKILL_NAME_PLACEHOLDER/$SKILL_NAME/g" "$SKILL_DIR/SKILL.md"

# Optional directories
if [[ "$CREATE_SCRIPTS" == true ]]; then
  mkdir -p "$SKILL_DIR/scripts"
  echo "# TODO: скрипт для $SKILL_NAME" > "$SKILL_DIR/scripts/.gitkeep"
  echo "  ├── scripts/"
fi

if [[ "$CREATE_REFERENCES" == true ]]; then
  mkdir -p "$SKILL_DIR/references"
  echo "# TODO: справочник для $SKILL_NAME" > "$SKILL_DIR/references/.gitkeep"
  echo "  ├── references/"
fi

if [[ "$CREATE_ASSETS" == true ]]; then
  mkdir -p "$SKILL_DIR/assets"
  echo "# TODO: ассеты для $SKILL_NAME" > "$SKILL_DIR/assets/.gitkeep"
  echo "  ├── assets/"
fi

echo ""
echo "✅ Skill создан: $SKILL_DIR/"
echo ""
echo "Структура:"
echo "  $SKILL_DIR/"
echo "  ├── SKILL.md"
[[ "$CREATE_SCRIPTS" == true ]]    && echo "  ├── scripts/"
[[ "$CREATE_REFERENCES" == true ]] && echo "  ├── references/"
[[ "$CREATE_ASSETS" == true ]]     && echo "  └── assets/"
echo ""
echo "Следующий шаг: отредактируй $SKILL_DIR/SKILL.md (замени все TODO)"
