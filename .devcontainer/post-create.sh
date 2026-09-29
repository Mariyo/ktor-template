#!/usr/bin/env bash
set -euo pipefail

# Named volumes (and anything else Docker sets up before we run) can leave parts of the
# home dir root-owned, which silently breaks whatever next tries to write there (already
# bit Kotlin LSP's ~/.cache once). Whole-home chown avoids finding the next one by hand.
sudo chown -R vscode:vscode /home/vscode

# Runs pre-commit checks without depending on an external hook manager.
git config core.hooksPath .githooks

# Warms the default cache plus the two the dev loop uses: configuration cache is keyed
# per --project-cache-dir, so without this the first F5 pays to configure the build twice.
./gradlew --quiet classes testClasses
./gradlew --quiet --project-cache-dir=.gradle/watch classes
./gradlew --quiet --project-cache-dir=.gradle/run classes

# Prevents Playwright's internal `apt-get install` from blocking on a prompt.
export DEBIAN_FRONTEND=noninteractive

# Chromium's host-side libraries are needed even when the binary is cached, so this always runs (see build.gradle.kts).
./gradlew --quiet playwrightInstallWithDeps
