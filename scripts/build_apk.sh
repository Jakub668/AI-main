#!/usr/bin/env bash
# helper script to compile the Android sample app and copy the resulting
# APK to the repo root for easy access.
set -e
cd "$(dirname "$0")/../android"
./gradlew assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk ../
echo "APK built and copied to project root: app-debug.apk"