#!/usr/bin/env bash
#
# Add (or replace) one benchmark run in `manifest.json`.
#
# The dashboard reads `manifest.json` to discover the per-run JMH JSON files in
# this folder. The weekly CI job calls this script after a benchmark run to
# register the freshly produced file, then commits both.
#
# Usage:
#   update-manifest.sh \
#       --file jmh-2026-06-15.json \
#       --date 2026-06-15T00:00:00Z \
#       --commit 1a2b3c4 \
#       --label "Weekly run 2026-06-15"
#
# It is idempotent: re-registering the same --file replaces its entry rather
# than duplicating it. Entries are kept sorted by date (oldest first).
#
# Requires `jq` (preinstalled on GitHub-hosted runners).

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MANIFEST="$SCRIPT_DIR/manifest.json"
TITLE="Pi4J FFM — JMH Performance Benchmarks"

FILE="" DATE="" COMMIT="" LABEL=""

usage() {
  sed -n '3,17p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --file)     FILE="$2";     shift 2 ;;
    --date)     DATE="$2";     shift 2 ;;
    --commit)   COMMIT="$2";   shift 2 ;;
    --label)    LABEL="$2";    shift 2 ;;
    --manifest) MANIFEST="$2"; shift 2 ;;
    -h|--help)  usage; exit 0 ;;
    *) echo "Unknown argument: $1" >&2; usage; exit 1 ;;
  esac
done

if [[ -z "$FILE" || -z "$DATE" ]]; then
  echo "error: --file and --date are required" >&2
  exit 1
fi
# Default label mirrors the Python version: "Run YYYY-MM-DD".
[[ -n "$LABEL" ]] || LABEL="Run ${DATE:0:10}"

command -v jq >/dev/null || { echo "error: jq is required" >&2; exit 1; }

# Start a fresh manifest if none exists yet.
if [[ ! -f "$MANIFEST" ]]; then
  printf '{\n  "title": "%s",\n  "runs": []\n}\n' "$TITLE" > "$MANIFEST"
fi

# Replace any existing entry for this file, append the new one, sort by date.
tmp="$(mktemp)"
jq \
  --arg file   "$FILE" \
  --arg date   "$DATE" \
  --arg commit "$COMMIT" \
  --arg label  "$LABEL" \
  --arg title  "$TITLE" '
    .title = (.title // $title)
    | .runs = (
        (.runs // [])
        | map(select(.file != $file))
        + [ { file: $file, date: $date, commit: $commit, label: $label } ]
        | sort_by(.date)
      )
  ' "$MANIFEST" > "$tmp"
mv "$tmp" "$MANIFEST"

count="$(jq '.runs | length' "$MANIFEST")"
echo "manifest.json now lists ${count} run(s); added ${FILE}"
