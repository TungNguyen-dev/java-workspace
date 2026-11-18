#!/usr/bin/env bash
set -euo pipefail
mvn -q clean install

rm -rf dist/lib && mkdir -p dist/lib dist/plugins
mvn -q -pl tool-hub-core dependency:copy-dependencies -DoutputDirectory=../dist/lib
cp tool-hub-core/target/tool-hub-core-*.jar dist/lib/
cp tool-hub-plugins/plugin-base64/target/plugin-base64-*.zip dist/plugins/

cd dist && java -cp "lib/*" tungnn.tutor.java.tool.hub.core.Application