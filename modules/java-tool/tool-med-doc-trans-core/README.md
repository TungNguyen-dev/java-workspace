# tool-med-doc-trans-core

Translates Traditional Chinese Medicine documents, such as textbooks scanned from print and OCR'd, from Chinese into a target language (Vietnamese by default). It writes a DOCX and a report that lists what was fixed, what was skipped and what still looks suspicious.

Every LLM step (`fix-ocr`, `translate`, `polish-target`, `format-target`) gets its own chat backend, an `LlmClient` (`java-ai`) or the Claude Code CLI (see [Choosing the LLM per step](#choosing-the-llm-per-step)); DOCX ↔ Markdown conversion comes from `java-document`. The module only uses `tool-document-translation-core` for `LanguageCode`: the translation is not done by its `TextTranslator`, and there is no translation cache for now.

## Workflow

```
source (.docx/.md/.markdown/.txt)
   │  load (always)                                ──► 01_load.md
   ▼
   │  fix-ocr:          OCR cleanup                ──► 02_fix_ocr.md
   ▼
   │  scan-source:      suspicious text (always)   ──► (report only)
   ▼
   │  structure-source: structure the Chinese text ──► 04_structure_source.md
   ▼
   │  glossary:         herb and formula names     ──► 05_glossary.md
   ▼
   │  translate:        LLM, one section per call  ──► 06_translate.md
   ▼
   │  polish-target:    friendlier style (VI)      ──► 07_polish_target.md
   ▼
   │  format-target:    Vietnamese format (VI)     ──► 08_format_target.md
   ▼
   │  scan-target:      suspicious text (always)   ──► (report only)
   ▼
   │  check-doses:      doses vs. the source       ──► 10_check_doses.md
   ▼
   │  export:           DOCX                       ──► 11_<name>_<language>.docx
```

`service.MedDocTranslationService.run` runs the selected steps in order. Every run writes into its own folder, and every step writes a new numbered file, so a step never overwrites the result of an earlier one (see [Output files](#output-files)). `report.md` is always written.

### Choosing the steps

`load`, `scan-source` and `scan-target` always run (a scan is skipped when it does not apply to the run) and cannot be selected. The other eight steps are selectable, and all of them run by default:

| Step | What it does |
| --- | --- |
| `fix-ocr` | OCR cleanup with the LLM, then a scan for suspicious text |
| `structure-source` | Structures the Chinese text into headings |
| `glossary` | Collects formula and herb names from the Chinese text and translates them once with the LLM, so the sections translated in parallel use the same names (needs `translate`, not for target `ZH`) |
| `translate` | Translates with the LLM |
| `polish-target` | Edits the Vietnamese translation into a friendlier style with the LLM (target `VI` only) |
| `format-target` | Reformats the Vietnamese translation (target `VI` only) |
| `check-doses` | Compares the doses and quantities of the translation with the source, section by section, without an LLM (needs `translate` in the same run, not for target `ZH`) |
| `export` | Converts the last Markdown file to DOCX |

A step that is not selected is skipped: it writes no file, and its number is left as a gap. Each step works on the output of the last step that ran, or on the loaded input when none ran. So you can start in the middle, for example give an already translated `.md` and run only `format-target,export`.

Two things to know when steps are left out:

- `format-target` matches the translated headings to the Chinese headings of `structure-source` in the same run, to set the section labels. Without `structure-source`, that one sub-step is skipped and reported ("no Chinese source in this run"); the other Vietnamese steps still run.
- Without `export`, the report says so and points to the last Markdown file written.

### Quality gates

After the load and after every selected step, `core.gate.QualityGateUtils` checks the output of that stage against the thresholds of `core.gate.GatePolicy`. When a gate fails, the run stops before the next stage: the files written so far stay in the run folder, `report.md` is written with every gate result, and a `QualityGateException` is thrown.

| Stage | Fails when | Default |
| --- | --- | --- |
| load | no text, or no Chinese text while `fix-ocr`, `structure-source` or `translate` will run | at least 1 Han character |
| `fix-ocr` | text blocks are lost, or the share of Han characters kept drops (the LLM translated or cut the text) | kept ≥ 0.95 |
| `structure-source` | no heading of level 1-3, or too many Han characters are lost | kept ≥ 0.80 (removed `文献摘要` and `病案` sections are not counted as lost) |
| `translate` | failed sections, block count outside the range, too many blocks still in Chinese (target other than `ZH`), or changed heading levels when `structure-source` and `format-target` (VI) both run | 0 failed, ratio 0.8-1.25, ≤ 5% in Chinese |
| `polish-target` | failed sections, block count outside the range, a changed number or level of headings, Chinese coming back | 0 failed, ratio 0.8-1.25 |
| `format-target` | fewer headings of level 1-3, failed LLM calls, too many blocks still in Chinese | 0 failed calls |
| `check-doses` | a section has doses that differ from the source (a missing, added or changed number next to a dose unit) | 0 sections |
| `export` | the DOCX is missing, empty or has no text when read back | none |

A refine step that matches nothing is still only a warning in the report. Pass a custom policy through the Java API: `new MedDocTranslationService(llmClient, new GatePolicy(...))`. From the command line a failed gate prints the report, then the reason, and exits with code 1.

### Load the input

`utils.SourceDocumentUtils.toMarkdown` reads `.docx` (through the DOCX → Markdown converter) and `.md`, `.markdown` or `.txt` (read as Markdown). Any other extension fails the run (the report is still written).

### fix-ocr

`core.ocr.OcrCleanup.clean`:

1. Normalizes the text with `MarkdownRefiner.forSource()`.
2. Turns ordered lists into plain paragraphs, keeping the numbers as `1、...`. A paragraph that starts with `1.` would be parsed back as a list; `1、` stays a paragraph, so the translation sees it as normal text. After the translation the numbers are restored to `1. ...`.
3. Sends every non-empty heading and paragraph to the LLM with the prompt `prompts/ocr-correct-chinese.md`, up to 5 blocks at a time.
4. Keeps the corrected blocks and records each changed block as an `core.ocr.OcrFix` (block index, before, after).

Then `core.inspect.SuspiciousTextUtils.scanSource` flags stray characters left after cleanup, paragraphs that look cut off, and herb composition lines with suspicious doses.

### structure-source

`core.refine.MedFormatPipelines.structureSource` works on the Chinese document, whose layout is stable and does not depend on the translation:

- merges `第N章` and the next paragraph into the chapter title (`#`), only when that paragraph looks like a name (short, no punctuation or leading number);
- removes the learning objectives under the chapter title: a run of short paragraphs numbered 1, 2, 3, ... Anything else (for example `3.5g ...`) is kept;
- turns `第N节 ...` paragraphs into `##` headings;
- turns the paragraph before `【组成】` into a `###` formula heading;
- turns every `【...】` paragraph into a `####` heading plus its content;
- removes the `#### 文献摘要` and `#### 病案` sections (up to the next heading of level 4 or higher). Their Han characters are left out of the retention base of the `structure-source` gate and shown as `removed Han` in its metrics.

Each step is wrapped by `safe(...)`: if a step throws, it is skipped, the document is left unchanged, and the step and the reason are listed in the report. The structure steps are also reported when they match nothing, so a layout the pipeline does not recognise is not silent.

### glossary

`core.glossary.GlossaryExtractor` runs on the Chinese document before `translate` (not for target `ZH`, and only when `translate` is selected). It collects the candidates without an LLM: the names of the `###` headings (formulas) and the herb names written before a dose such as `石膏30g`. It sends them to the LLM in batches of 80 with the prompt `prompts/glossary-tcm-terms.md`, which answers a table `中文 | translation`, and keeps the answers in a `core.glossary.Glossary`.

`translate` then adds to its system prompt, for each section, only the terms that appear in that section. The sections are translated in parallel, so this keeps the herb and formula names the same everywhere. The glossary is written to `05_glossary.md` for review. If a batch fails, the run goes on with the partial glossary and the report lists it in the skipped list.

### translate

`core.translator.LlmMarkdownTranslator` splits the document into sections and sends each section to `LlmClient.chat` as one long Markdown text:

- a section starts at every heading of level 1 to 3 (for example one formula = `###` with its `####` parts). A `###` formula section is sent whole (up to 15000 characters, then split at its `####` sub-headings); any other section longer than 4000 characters is split again between blocks;
- up to 5 sections are translated at the same time, and the results are put back in order;
- the system prompt is `prompts/translate-tcm-text.md`, with the target language filled in. It asks the model to keep the Markdown structure, the numbering and the dose units;
- if a call fails or answers nothing, that section is kept in the original language, and the report counts it and shows the first error. If the answer changes the heading levels, the section is kept but the report gets a warning, because the section labels of `format-target` rely on the same headings;
- list numbers are normalised to plain `1. text` paragraphs.

### polish-target

`core.translator.LlmMarkdownTranslator.editor(...)` runs on the translated document (target `VI` only). It reuses the section chunking, the concurrency and the "keep the section on failure" rule of `translate`, with the system prompt `prompts/polish-vietnamese.md`: rewrite stiff, word-by-word sentences into natural modern Vietnamese for students and practitioners, keeping the facts, the Hán-Việt terms, the doses and the Markdown structure. The step must not change the headings, because `format-target` sets the section labels from the Chinese headings by position; a changed heading level fails the gate. Sections whose call fails are kept as translated and listed in the report.

### format-target

`core.refine.MedFormatPipelines.formatTarget` runs on the translated document (target `VI` only). It is a `core.markdown.MarkdownPipeline` of `core.markdown.MarkdownAction` steps that:

- set the section labels from a fixed dictionary, matching the k-th heading of the translation to the k-th heading of the Chinese source (`relabelHeadings`). If the heading counts or levels differ, the step is skipped and the reason is reported;
- add `## Đại cương`, turn ordered lists into paragraphs and upper-case the formula names;
- use the LLM to turn `Thành phần` into a table, normalize `Phụ phương`, and rewrite `Cách dùng` as modern usage instructions. When one call fails, that formula is kept unchanged and the next one goes on; the failure is reported with the formula name;
- normalize the text with `MarkdownRefiner.defaultMedical()`.

| Source label | Vietnamese label |
| --- | --- |
| 组成 | Thành phần |
| 用法 | Cách dùng |
| 功用 | Công dụng |
| 主治 | Chủ trị |
| 方解 | Giải nghĩa bài thuốc |
| 运用 | Vận dụng lâm sàng |
| 附方 | Phụ phương |

After `translate` and `format-target`, `core.inspect.SuspiciousTextUtils.scanTranslation` flags Chinese text that is still in the output. It is skipped when the target is `ZH`.

### check-doses

`core.inspect.DoseCheckUtils` compares the translation with the Chinese text that `translate` received, without an LLM. It reads every number that sits next to a dose unit (`克`, `g`, `钱`, `两`, `分`, `枚`, `片`, `斤`, `ml` and their Vietnamese forms such as `chỉ`, `lạng`, `cân`; Chinese numerals and `半` / `nửa` are understood, and ranges give two numbers). The documents are split at the `###` headings and compared section by section as a multiset of numbers; when the two documents do not have the same number of `###` sections, the whole document is compared at once. The units are not converted, so a translation must keep the numbers of the source.

The step is skipped when `translate` did not run in the same run, or the target is `ZH`. It writes `10_check_doses.md` with the sections that differ, and the gate fails above `GatePolicy.maxDoseMismatches` (default 0).

### export

`MarkdownDocxConverterUtils` converts the last Markdown file of the run to DOCX.

## Output files

Each run creates its own folder `<out>/<yyyyMMdd_HHmmss>/` (a suffix `_1`, `_2`, ... is added if the name is already taken). Every step writes a new file, so you can open any step and compare it with the next one. `<name>` is the source file name without its extension.

| File | Written by | Content |
| --- | --- | --- |
| `01_load.md` | load | Source converted to Markdown |
| `02_fix_ocr.md` | `fix-ocr` | Source after OCR cleanup, still in Chinese |
| `04_structure_source.md` | `structure-source` | Chinese source after the structure refine. This is the file that gets translated |
| `05_glossary.md` | `glossary` | Glossary of the names used for the translation (not the document: later steps ignore it) |
| `06_translate.md` | `translate` | Translation |
| `07_polish_target.md` | `polish-target` | Translation after the style edit |
| `08_format_target.md` | `format-target` | Translation after the Vietnamese refine |
| `10_check_doses.md` | `check-doses` | Table of the sections whose doses differ (not the document) |
| `11_<name>_<suffix>.docx` | `export` | Final DOCX. Suffix: `tieng_Viet` (VI), `tieng_Anh` (EN), `tieng_Nhat` (JA), `tieng_Trung` (ZH) |
| `report.md` | always | Report: steps, quality gate results, a `## Steps` section with one entry per step (status, output file, block counts, time, gate, details and LLM usage), run folder, target language, sections translated and failed, OCR fixes, skipped refine steps, translation warnings and suspicious text (for the target `VI`, this includes any remaining `tễ`, which should read `bài thuốc`). Each suspect names the file its block number refers to. When the run fails, the report is still written with `Output: none` and the error |

The numbers are fixed per step, so a step that did not run leaves a gap.

## Packages

Root package: `tungnn.tutor.java.tool.meddoc`. The code is split into layers, and a layer only imports the layers below it (checked by `LayerDependencyTest`):

| Layer | May import |
| --- | --- |
| `utils` | nothing |
| `core` | `utils` |
| `llm` | `core`, `utils` |
| `service` | `llm`, `core`, `utils` |
| root (`MedDocTranslationApp`) | everything |

| Package | Purpose |
| --- | --- |
| `meddoc` | `MedDocTranslationApp`, the command-line entry point |
| `service` | The public API: `MedDocTranslationService` (the workflow), `MedDocTranslationRequest` |
| `service.report` | `MedDocReport`, `StepReport`, `StepStatus`: the content of `report.md` |
| `llm` | LLM backends and their usage metering: `ChatSpec`, `ChatSpecUtils`, `StepChats`, `ClaudeCliMarkdownChat`, `LlmClientMarkdownChat`, `ChatUsage` |
| `utils` | `SourceDocumentUtils`: load a source file as Markdown |
| `core` | The domain: `Step`, and the packages below |
| `core.chat` | `MarkdownChat`: the port every LLM backend implements |
| `core.pipeline` | One `StepRunner` per step (`load`, `fix-ocr`, `scan-source`, `structure-source`, `glossary`, `translate`, `polish-target`, `format-target`, `scan-target`, `check-doses`, `export`) and the shared `PipelineState` |
| `core.glossary` | `Glossary`, `GlossaryExtractor`: the term table used by `translate` |
| `core.ocr` | `OcrCleanup`, `OcrFix`: LLM-based OCR correction |
| `core.inspect` | `SuspiciousTextUtils`, `Suspect`: detect text that needs a manual check |
| `core.translator` | `LlmMarkdownTranslator`: translation through `MarkdownChat`, section by section |
| `core.markdown` | `MarkdownAction`, `MarkdownActions`, `MarkdownPipeline`, `MarkdownRefiner`: Markdown editing building blocks |
| `core.gate` | `GatePolicy`, `GateResult`, `QualityGateUtils`, `QualityGateException`: the quality gate of each stage |
| `core.refine` | `MedFormatPipelines`: the structure recipe for the Chinese text and the refine recipe for Vietnamese |

## Prompts

Stored in `src/main/resources/prompts/`.

| File | Used by |
| --- | --- |
| `ocr-correct-chinese.md` | `OcrCleanup` (`fix-ocr`) |
| `translate-tcm-text.md` | `LlmMarkdownTranslator` (`translate`) |
| `polish-vietnamese.md` | System prompt of the `polish-target` step |
| `format-ingredients-table.md` | `Thành phần` section of `MedFormatPipelines` (`format-target`) |
| `format-variant-formulas.md` | `Phụ phương` section of `MedFormatPipelines` (`format-target`) |
| `format-usage-instructions.md` | `Cách dùng` section of `MedFormatPipelines` (`format-target`) |

## Usage

### Run the tool

You need Java 25, Maven, and an API key for the LLM provider in an env file.

**1. Configure the provider.** `dev.env` at the repo root (copy `.env.example` if you do not have it yet) must contain the key of the provider you use. The app uses `deepseek` unless `DOCTRANS_TRANSLATOR_PROVIDER` says otherwise:

```
DOCTRANS_TRANSLATOR_PROVIDER=deepseek
DEEPSEEK_API_KEY=...
```

**2. Build once** (and again after you change another module), from the repo root. This installs the modules that this one depends on:

```
mvn -DskipTests install -pl modules/java-tool/tool-med-doc-trans-core -am
```

**3. Run** from the repo root. Load the env file into the shell first, then start the app with `exec:exec` (the app's `main` is not public, so `exec:java` cannot start it):

```
set -a; source dev.env; set +a

mvn -q -pl modules/java-tool/tool-med-doc-trans-core compile exec:exec \
  -Dexec.executable=java -Dexec.workingdir="$PWD" \
  -Dexec.args="-cp %classpath tungnn.tutor.java.tool.meddoc.MedDocTranslationApp \"storage/book/Chương 3.docx\" --lang VI --out storage/book/out"
```

**Or run the jar.** Build the self-contained jar, from the repo root:

```
mvn -DskipTests package -pl modules/java-tool/tool-med-doc-trans-core -am
```

This writes `modules/java-tool/tool-med-doc-trans-core/target/tool-med-doc-trans-core-0.0.1-SNAPSHOT-all.jar`. Run it from the repo root, after loading the env file:

```
set -a; source dev.env; set +a

java -jar modules/java-tool/tool-med-doc-trans-core/target/tool-med-doc-trans-core-0.0.1-SNAPSHOT-all.jar \
  "storage/book/Chương 3.docx" --lang VI --out storage/book/out
```

The arguments are the same as in "Command line" below, written as normal shell arguments (no `-Dexec.args`). Relative paths resolve from the current folder. The jar can be copied anywhere; it only needs Java 25 and the env variables.

Notes:

- Everything after `MedDocTranslationApp` inside `-Dexec.args` is the application's own arguments (see the table below). Put a path with spaces in `\"...\"` as above.
- `-Dexec.workingdir="$PWD"` makes relative paths (the input and `--out`) resolve from the repo root; without it they resolve from the module folder.
- The result is in `<out>/<yyyyMMdd_HHmmss>/`; the report is also printed to the console. Without `--out`, the run folder goes next to the input file.
- A real run makes billed LLM calls. To check the setup without any call, run only the `export` step: `--steps export`.
- Alternatively, run `MedDocTranslationApp` from the IDE with the same program arguments, and set the env variables of `dev.env` in the run configuration.
- The same workflow is available in the dev-desk app as the "Medical Document Translator" tool (plugin `plugin-med-doc-trans`); see "Run dev-desk" in the root `README.md`.

### Command line

Arguments of `MedDocTranslationApp`:

```
MedDocTranslationApp <input.docx|md|txt> [--lang VI] [--out dir] [--steps fix-ocr,structure-source,glossary,translate,polish-target,format-target,check-doses,export] [--no-refine]
```

| Option | Default | Meaning |
| --- | --- | --- |
| `--lang` | `VI` | Target language code (`VI`, `EN`, `JA`, `ZH`, ...) |
| `--out` | folder of the input file | Directory that holds the run folders |
| `--steps` | all steps | Comma separated steps to run |
| `--no-refine` | refine on | Removes `structure-source`, `polish-target` and `format-target` from the steps |

Argument examples (put them after `MedDocTranslationApp` in `-Dexec.args`, or after the jar path):

```
# everything
"Chương 3.docx"

# only clean the OCR and look at 02_fix_ocr.md
"Chương 3.docx" --steps fix-ocr

# translate an already cleaned file, no DOCX
02_fix_ocr.md --steps structure-source,translate,format-target

# translate to English, without the refine steps
"Chương 3.docx" --lang EN --no-refine
```

A missing option value, an unknown language code or an unknown step prints the reason and the usage, and exits with code 1. A failed quality gate also exits with code 1 (after printing the report) (with `exec:exec`, Maven then reports "Command execution failed", which is expected).

### Java API

```java
var service = new MedDocTranslationService(llmClient);
var report = service.run(new MedDocTranslationRequest(
        Path.of("book.docx"), LanguageCode.VI, Path.of("out"), Step.all()));
System.out.println(report.render());
```

`MedDocTranslationRequest` takes the set of steps (`null` means all of them). The report's `runDir()` is the folder of this run, `outputDocx()` is `null` when `export` did not run, and `finalFile()` is the last file written. The same service is used by the `MedDocTranslateTool` in `plugin-med-doc-trans` (dev-desk): its `steps` parameter takes the same comma separated names, `refineVietnamese=false` removes the two refine steps, and it writes the run folders under `<dataDir>/runs/` and keeps them. When `export` is not selected it returns the last Markdown file instead of a DOCX.

## Configuration

The app reads the provider from the environment:

| Variable | Purpose |
| --- | --- |
| `DOCTRANS_TRANSLATOR_PROVIDER` | LLM provider name. Defaults to `deepseek` when unset |
| Provider key, e.g. `DEEPSEEK_API_KEY` | API key for the chosen provider |

See `.env.example` at the repo root for the provider options.

## Extending the refine pipeline

Add a step in `MedFormatPipelines.vietnamese`:

```java
.step("Describe the step", safe(skipped, "short name", MarkdownActions.renameHeading(4, "Old", "New")))
```

For an LLM-based step, use `MarkdownActions.llmSection(headingText, level, llmClient, prompt, onError)` with a new prompt file in `prompts/`.

## Choosing the LLM per step

Each LLM step has its own client, and you choose the backend and the model of each one. The spec is `<backend>[:<model>]`, plus an optional `@<effort>` (`low`, `medium`, `high`, `xhigh` or `max`) for `claude-cli` only, where the backend is `claude-cli` (the `claude -p` command line, which uses a Claude Code login and no API key) or a `java-ai` provider such as `deepseek`, `anthropic`, `openai` or `ollama`. The model is everything after the first `:`, so `ollama:qwen3:8b` is the model `qwen3:8b`. Without a model, the default of the backend is used. The `@<effort>` suffix wins over `CLAUDE_CLI_EFFORT`, so `claude-cli:sonnet@low` and `claude-cli@low` set the effort of one step.

```
java -jar tool-med-doc-trans-core-*-all.jar book.docx \
    --llm default=deepseek --llm translate=claude-cli:sonnet@medium --llm polish-target=gemini
DOCTRANS_LLM_TRANSLATE=claude-cli:sonnet java -jar ... book.md --steps translate,export
```

Per step, the first value found wins: `--llm <step>=<spec>`, then `DOCTRANS_LLM_<STEP>` (`FIX_OCR`, `GLOSSARY`, `TRANSLATE`, `POLISH_TARGET`, `FORMAT_TARGET`), then the default. The default is `--llm default=<spec>`, then `DOCTRANS_LLM_DEFAULT`, then `DOCTRANS_TRANSLATOR_PROVIDER`, then `deepseek`. `structure-source`, `check-doses` and `export` do not use an LLM and cannot be configured.

When the `translate` step uses Claude (`claude-cli` or the `anthropic` provider), the `polish-target` step is always skipped, even when it is selected with `--steps` or configured with `--llm polish-target=...`. This rule has priority over every other setting, applies whatever the steps of the run are, and is written in the skipped list of `report.md`. Running only `--steps polish-target` in that case fails because nothing is left to run.

A client is created only when its step runs, so a missing API key only matters for the steps that need it. With `--steps translate,export --llm translate=claude-cli`, no API key is needed at all.

Claude CLI calls run in an empty working directory with no tools, MCP servers, slash commands or user settings, so `CLAUDE.md` files and hooks do not reach the prompt. Every section is one call: the prompt is the system prompt, the Markdown section goes through stdin, and the reply is read from the JSON output.

| Variable | Default | Purpose |
| --- | --- | --- |
| `CLAUDE_CLI_PATH` | `claude` | Path of the executable, needed when it is not on the `PATH` (IDE launches on macOS) |
| `CLAUDE_CLI_MODEL` | `sonnet` | Model when the spec has none |
| `CLAUDE_CLI_EFFORT` | `medium` | `low`, `medium`, `high`, `xhigh` or `max` |
| `CLAUDE_CLI_TIMEOUT_SECONDS` | `600` | Timeout of one call, the process is killed after it |
| `DOCTRANS_CLAUDE_CLI_CONCURRENCY` | `2` | Parallel Claude CLI calls; keep it low for subscription rate limits |

Each run lists the calls and tokens (input, cache, output, thinking) of every step that uses an LLM, with any backend, in its entry of the `## Steps` section and in the `## LLM usage` section of `report.md` (with a total), and in the log. The cost is only known for `claude-cli`; other backends show `$n/a`, and a provider that returns no usage data shows `tokens not reported by provider`. `in` always includes the cached tokens, `cache` is the part of it that was read from the cache. A call that fails after the model ran (for example a `claude-cli` answer cut at `max_tokens`) is counted as failed with its tokens and cost; a call that throws before any answer, or a backend API error, has no usage data. A step that fails still shows the usage of its calls. A real section measured on `sonnet` with `medium` effort costs about a third of `opus` with `high` effort and uses no thinking tokens.

A failed call (usage limit, timeout, refusal, truncation at `max_tokens`) is a failed section: the original text is kept and the quality gate of that step stops the run.
