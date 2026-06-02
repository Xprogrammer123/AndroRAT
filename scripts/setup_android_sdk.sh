#!/usr/bin/env bash
# Install Android SDK cmdline-tools, accept licenses, install API 35 + build-tools.
set -euo pipefail

export ANDROID_HOME="${ANDROID_HOME:-$HOME/Android/Sdk}"
mkdir -p "$ANDROID_HOME/cmdline-tools"

SDKMANAGER="$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager"
if [[ ! -x "$SDKMANAGER" ]]; then
  echo "Downloading Android command-line tools..."
  TMP="$(mktemp -d)"
  wget -q -O "$TMP/cmdline-tools.zip" \
    "https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
  unzip -qo "$TMP/cmdline-tools.zip" -d "$TMP"
  rm -rf "$ANDROID_HOME/cmdline-tools/latest"
  mv "$TMP/cmdline-tools" "$ANDROID_HOME/cmdline-tools/latest"
  rm -rf "$TMP"
fi

export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

echo "Accepting SDK licenses (type 'y' if prompted)..."
yes | sdkmanager --licenses

echo "Installing platform 35 and build-tools..."
sdkmanager "platform-tools" "platforms;android-35" "build-tools;34.0.0"

PROPS="$(cd "$(dirname "$0")/../Android_Code" && pwd)/local.properties"
echo "sdk.dir=$ANDROID_HOME" > "$PROPS"
echo "Done. SDK at $ANDROID_HOME"
echo "Now run: bash AndroRAT/scripts/refresh_compiled_apk.sh"
