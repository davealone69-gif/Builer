#!/usr/bin/env sh

# -----------------------------------------------------------------------------
# Minimal Gradle wrapper script for CI/local builds in environments where the
# generated wrapper is missing. Delegates to installed Gradle.
# -----------------------------------------------------------------------------

set -eu

exec gradle "$@"
