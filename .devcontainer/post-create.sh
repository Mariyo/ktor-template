#!/usr/bin/env bash
set -euo pipefail

# Named volumes (and anything else Docker sets up before we run) can leave parts of the
# home dir root-owned, which silently breaks whatever next tries to write there (already
# bit Kotlin LSP's ~/.cache once). Whole-home chown avoids finding the next one by hand.
sudo chown -R vscode:vscode /home/vscode

# Runs pre-commit checks without depending on an external hook manager.
git config core.hooksPath .githooks

# Prevents Playwright's internal `apt-get install` from blocking on a prompt.
export DEBIAN_FRONTEND=noninteractive

# Chromium's host-side libraries are required even when the browser binary is cached; install
# the exact native packages Playwright validates before each browser launch in a fresh container.
sudo apt-get update
sudo apt-get install -y --no-install-recommends \
  libevent-2.1-7 \
  libopus0 \
  libgstreamer-gl1.0-0 \
  libgstreamer-plugins-bad1.0-0 \
  libflite1 \
  libavif15 \
  libharfbuzz-icu0 \
  libwebpmux3 \
  libmanette-0.2-0 \
  libhyphen0 \
  libwoff1 \
  libgles2 \
  libx264-164

# Warms the default cache plus the two the dev loop uses: configuration cache is keyed
# per --project-cache-dir, so without this the first F5 pays to configure the build twice.
./gradlew --quiet classes testClasses
./gradlew --quiet --project-cache-dir=.gradle/watch classes
./gradlew --quiet --project-cache-dir=.gradle/run classes

# Downloads the Chromium binary and its OS-level dependencies in one step (see build.gradle.kts).
./gradlew --quiet playwrightInstall
