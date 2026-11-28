# tool-todo-task-core

Todo tasks whose source of truth is the markdown notes of an Obsidian vault. Tasks are inline checkboxes in the format of the Obsidian Tasks plugin, found in any note of the vault:

```
- [ ] Review PR ⏫ ➕ 2026-11-28 📅 2026-12-01 #work
- [x] Pay bill ✅ 2026-11-27
```

The vault is read and written directly on the filesystem, so Obsidian does not have to be running.

## Run

```
java -jar target/tool-todo-task-core-*-all.jar list   [--vault dir] [--status todo|done|all] [--due-before d] [--overdue] [--tag t] [--path p] [--text s]
java -jar target/tool-todo-task-core-*-all.jar add    "<description>" [--note Inbox.md] [--heading h] [--due d] [--priority high] [--tag t]...
java -jar target/tool-todo-task-core-*-all.jar done   <id>
java -jar target/tool-todo-task-core-*-all.jar undone <id>
java -jar target/tool-todo-task-core-*-all.jar edit   <id> [--text s] [--due d|none] [--priority p|none] [--add-tag t] [--remove-tag t]
java -jar target/tool-todo-task-core-*-all.jar delete <id> [--with-children]
```

Environment variables: `TODO_VAULT_DIR` (required unless `--vault` is given), `TODO_INBOX_NOTE` (default `Inbox.md`; may contain a date pattern such as `Daily/{yyyy-MM-dd}.md`, expanded with today's date, also for `--note`), `TODO_INBOX_HEADING`, `TODO_EXCLUDES` (comma separated vault-relative paths), `TODO_GLOBAL_FILTER` (e.g. `#task`: only lines containing it count as tasks).

## Task ids

`list` prints each task with an id `path:line#hash`. The hash covers the raw line, so a command fails with "Task changed since it was listed" when the note was edited in the meantime. Every command that changes a task prints its new id.

## Behavior

- Only the touched line changes; every other line, the line endings and unknown tokens (`🔁 every week`, `🆔`, `⛔`) are kept as they are. Recurring tasks are not rolled over by `done`.
- `edit --text` replaces the description up to the first date, priority, unknown marker or trailing tag.
- `delete` refuses a task that has indented sub-items unless `--with-children` is given.
- Hidden folders (`.obsidian`, `.trash`), `TODO_EXCLUDES` and fenced code blocks are skipped.
- Supported prefixes: `-`, `*`, `+`, numbered items and blockquote/callout lines.
