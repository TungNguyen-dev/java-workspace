# java-workspace

## Run dev-desk

```bash
mvn -pl modules/tool-dev-desk/tool-dev-desk-app -am clean package -DskipTests

cp modules/tool-dev-desk/tool-dev-desk-app/target/tool-dev-desk-app-1.0-SNAPSHOT.jar tools/bin/

tools/run-jar.sh -l

tools/run-jar.sh tool-dev-desk-app-1.0-SNAPSHOT
```

`tools/run-jar.sh` loads `dev.env` (override with `ENV_FILE=path/to/env`), then runs `java -jar` under `caffeinate`. Jar lookup: a file path, then `tools/bin/<name>`, then `tools/bin/<name>.jar`.

Run from the repo root. `HUB_PLUGINS_DIR` and `HUB_DATA_DIR` in `dev.env` must be absolute paths to `modules/tool-dev-desk/dist/plugins` and `modules/tool-dev-desk/dist/data`; relative paths resolve against the working directory.

| Variable | Default | Meaning |
| --- | --- | --- |
| `HUB_PORT` | `8080` | HTTP port |
| `HUB_BIND_ADDRESS` | `127.0.0.1` | Bind address |
| `HUB_PLUGINS_DIR` | `plugins` | Plugin directory |
| `HUB_DATA_DIR` | `data` | Data directory |
| `HUB_WATCH` | `true` | Hot-reload plugins |
| `HUB_USERNAME` / `HUB_PASSWORD` | `admin` / empty | Login credentials |

Open http://127.0.0.1:8080 after startup.

## Run tool-selenium-browser-setup

```bash
mvn -pl modules/java-tool/tool-selenium-browser-setup -am clean package -DskipTests

cp modules/java-tool/tool-selenium-browser-setup/target/tool-selenium-browser-setup-0.0.1-SNAPSHOT-all.jar tools/bin/

tools/run-jar.sh tool-selenium-browser-setup-0.0.1-SNAPSHOT-all

BROWSER_SETUP_MODE=login tools/run-jar.sh tool-selenium-browser-setup-0.0.1-SNAPSHOT-all
```

Use the `-all` jar (the shaded executable one). Run from the repo root; `tools/run-jar.sh` loads `dev.env` the same way as above. Press Enter in the terminal to close the browsers.

- Default mode: opens Gemini, Coursera and YouTube in ChromeDriver-controlled Chrome profiles (`profile_0..N`) so you can sign in once.
- `BROWSER_SETUP_MODE=login`: starts plain Chrome (no ChromeDriver) on the same profiles. Use it for Google sign-in, which blocks automated browsers, then run the default mode and the session is reused.

| Variable | Default | Meaning |
| --- | --- | --- |
| `BROWSER_SETUP_MODE` | empty | `login` for plain Chrome without ChromeDriver |
| `WEBDRIVER_POOL_MAX_TOTAL` | see `WebDriverPoolConfig` | Number of browsers/profiles to open |
| `WEBDRIVER_POOL_PROFILE_PREFIX` | `profile_` | Profile name prefix |
| `WEBDRIVER_POOL_HEADLESS` | `false` | Headless Chrome (default mode only) |
| `WEBDRIVER_CHROME_BINARY_PATH` | Chrome for Testing in `~/.data/selenium/chrome` | Chrome binary (use regular Chrome if Google still rejects sign-in) |
| `WEBDRIVER_CHROME_DRIVER_PATH` | `~/.data/selenium/chrome/chromedriver-mac-x64/chromedriver` | ChromeDriver, must match the Chrome major version |
| `WEBDRIVER_CHROME_PROFILE_ROOT_DIR` | `~/.data/selenium/chrome/chrome-profiles` | Where profiles are stored |

## Run tool-med-doc-trans-core

```bash
mvn -pl modules/java-tool/tool-med-doc-trans-core -am clean package -DskipTests

cp modules/java-tool/tool-med-doc-trans-core/target/tool-med-doc-trans-core-0.0.1-SNAPSHOT-all.jar tools/bin/

tools/run-jar.sh tool-med-doc-trans-core-0.0.1-SNAPSHOT-all "<path/file.docx>"
```

Use the `-all` jar (the shaded executable one). Run from the repo root so relative paths resolve from there; `tools/run-jar.sh` loads `dev.env` the same way as above, and `caffeinate` keeps the Mac awake during a long run. The result goes to `<out>/<yyyyMMdd_HHmmss>/` (next to the input file without `--out`).

| Option | Default | Meaning |
| --- | --- | --- |
| `--lang` | `VI` | Target language code (`VI`, `EN`, `JA`, `ZH`, ...) |
| `--out` | folder of the input file | Directory that holds the run folders |
| `--steps` | all steps | Comma separated: `fix-ocr,structure-source,glossary,translate,polish-target,format-target,check-doses,export` |
| `--no-refine` | refine on | Removes `structure-source`, `polish-target` and `format-target` |

| Variable | Default | Meaning |
| --- | --- | --- |
| `DOCTRANS_TRANSLATOR_PROVIDER` | `deepseek` | LLM provider |
| Provider key, e.g. `DEEPSEEK_API_KEY` | none | API key; required even for `--steps export` |

Details of each step: `modules/java-tool/tool-med-doc-trans-core/README.md`.
