#!/bin/sh
file=$(python3 -I -c 'import json,sys; print(json.load(sys.stdin).get("tool_input",{}).get("file_path",""))')
case "$file" in
  *.java) ;;
  *) exit 0 ;;
esac
bin=$(command -v open-java-format || echo "$HOME/.local/bin/open-java-format")
[ -x "$bin" ] || exit 0
exec "$bin" --skip-removing-unused-imports --replace "$file"
