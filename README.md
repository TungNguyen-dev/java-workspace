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
