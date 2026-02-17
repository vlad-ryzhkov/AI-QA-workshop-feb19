#!/usr/bin/env bash
# Generate real TOCs for markdown files

set -euo pipefail

generate_toc() {
    local file="$1"
    local in_code_block=false

    while IFS= read -r line; do
        # Track code blocks
        if [[ "$line" =~ ^\`\`\` ]]; then
            in_code_block=$([[ "$in_code_block" == "false" ]] && echo "true" || echo "false")
            continue
        fi

        # Skip lines in code blocks
        [[ "$in_code_block" == "true" ]] && continue

        # Skip TOC section itself
        [[ "$line" =~ ^##[[:space:]]*Table[[:space:]]of[[:space:]]Contents ]] && continue
        [[ "$line" =~ ^\*TODO:.*TOC ]] && continue

        # Match headers (## to ####, but not #)
        if [[ "$line" =~ ^(###+)[[:space:]]+(.+)$ ]]; then
            local level="${BASH_REMATCH[1]}"
            local title="${BASH_REMATCH[2]}"

            # Remove markdown formatting from title
            title=$(echo "$title" | sed -E 's/`([^`]+)`/\1/g; s/\*\*([^*]+)\*\*/\1/g; s/\*([^*]+)\*/\1/g')

            # Generate anchor (GitHub style)
            local anchor=$(echo "$title" | tr '[:upper:]' '[:lower:]' | sed -E 's/[^a-z0-9а-яё -]+//g; s/ /-/g; s/--+/-/g; s/^-//; s/-$//')

            # Calculate indent (## = 0, ### = 2, #### = 4)
            local indent_count=$(( (${#level} - 2) * 2 ))
            local indent=$(printf '%*s' "$indent_count" '')

            echo "${indent}- [${title}](#${anchor})"
        fi
    done < "$file"
}

replace_toc() {
    local file="$1"
    local toc_file="$2"

    # Create temp file
    local temp_file="${file}.toc_tmp"

    # Read file and replace TOC placeholder
    awk '
        BEGIN { in_toc = 0; skip_empty = 0 }
        /^## Table of Contents/ {
            print "## Table of Contents"
            print ""
            # Read and print TOC from file
            while ((getline line < "'"$toc_file"'") > 0) {
                print line
            }
            close("'"$toc_file"'")
            in_toc = 1
            skip_empty = 1
            next
        }
        in_toc && /^\*TODO:.*TOC/ {
            in_toc = 0
            skip_empty = 1
            next
        }
        skip_empty && /^[[:space:]]*$/ {
            next
        }
        skip_empty && /^[^[:space:]]/ {
            skip_empty = 0
            print ""
            print
            next
        }
        { print }
    ' "$file" > "$temp_file"

    mv "$temp_file" "$file"
}

# Find all files with TOC placeholder
files_with_placeholder=(
    "docs/ai-files-handbook.md"
    "docs/ai-setup.md"
    ".claude/skills/init-skill/SKILL.md"
    ".claude/skills/test-plan/SKILL.md"
    ".claude/skills/repo-scout/SKILL.md"
    ".claude/skills/screenshot-analyze/SKILL.md"
    ".claude/skills/output-review/SKILL.md"
    ".claude/qa_agent.md"
)

echo "🔧 Generating real TOCs..."
echo ""

for file in "${files_with_placeholder[@]}"; do
    if [[ ! -f "$file" ]]; then
        echo "⚠️  [SKIP] $file (not found)"
        continue
    fi

    # Check if file has TOC placeholder
    if ! grep -q "TODO:.*TOC" "$file" 2>/dev/null; then
        echo "✓ [SKIP] $file (no placeholder)"
        continue
    fi

    echo "📝 Processing $file..."

    # Generate TOC to temp file
    toc_tmp="/tmp/toc_$$.txt"
    generate_toc "$file" > "$toc_tmp"

    if [[ ! -s "$toc_tmp" ]]; then
        echo "   ⚠️  No headers found, skipping"
        rm -f "$toc_tmp"
        continue
    fi

    replace_toc "$file" "$toc_tmp"
    item_count=$(wc -l < "$toc_tmp" | xargs)
    rm -f "$toc_tmp"

    echo "   ✅ TOC generated ($item_count items)"
done

echo ""
echo "✅ Done. Review with: git diff"
