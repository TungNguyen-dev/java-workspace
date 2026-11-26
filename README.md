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
