<!-- CODEGRAPH_START -->
## CodeGraph

In repositories indexed by CodeGraph (a `.codegraph/` directory exists at the repo root), reach for it BEFORE grep/find or reading files when you need to understand or locate code:

- **MCP tool** (when available): `codegraph_explore` answers most code questions in one call — the relevant symbols' verbatim source plus the call paths between them, including dynamic-dispatch hops grep can't follow. Name a file or symbol in the query to read its current line-numbered source. If it's listed but deferred, load it by name via tool search.
- **Shell** (always works): `codegraph explore "<symbol names or question>"` prints the same output.

If there is no `.codegraph/` directory, skip CodeGraph entirely — indexing is the user's decision.
<!-- CODEGRAPH_END -->

## Code generation

- Do not write documentation comments (Javadoc `/** ... */`, `@param`/`@return` tags) or header comments above classes, methods or fields when generating or editing code. Let names and types carry the meaning.
- Inline `//` comments inside method bodies are allowed only for important logic: a non-obvious reason, constraint, workaround or trade-off. Do not narrate what the code already says.
- Do not add comments to existing code you touch, and do not remove ones that are already there unless asked.
- Write all code comments and message strings (exception messages, log messages, console output, validation and UI texts) in English. Keep Vietnamese only where it is data the code operates on, such as regex patterns, matched document headings, prompts, and test fixtures.
