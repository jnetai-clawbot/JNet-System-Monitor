# J~Net System Monitor

A compact, dark Android system-monitoring dashboard built with Kotlin and Jetpack Compose.

Package name: `com.jnet.systemmonitor`

## About

- **Created By:** J~Net 2026
- **Website:** [jnetai.com](https://jnetai.com)
- **Version:** v1.0.0 (GitHub tag in full)

## Features

- Device manufacturer, model and Android version
- API level and device uptime
- CPU details where Android exposes them, plus core count
- RAM total, available and used
- Internal storage total, available and used
- Battery percentage, charging state and temperature
- Automatic dashboard refresh every 5 seconds
- Manual refresh button
- Inline status area instead of pop-up dialogs
- Dark interface (`#121212` background, `#1E1E1E` cards, `#4CAF50` accent)
- No unnecessary permissions, no ads, no tracking, no external services

Information that a device or Android version does not expose is labelled
`Unavailable` rather than being guessed.

## Repository structure

```text
.
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/jnet/systemmonitor/
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/SystemInfoProvider.kt
│       │   │   ├── data/SystemInfo.kt
│       │   │   ├── ui/DashboardScreen.kt
│       │   │   ├── ui/theme/Theme.kt
│       │   │   └── util/FormatUtils.kt
│       │   └── res/
│       └── test/java/com/jnet/systemmonitor/util/FormatUtilsTest.kt
├── .github/workflows/
│   ├── android-build.yml
│   └── android-release.yml
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

## Builds happen only on GitHub Actions

This repository intentionally does not require a local Android SDK or local
Gradle build. Every build, test and APK package step runs on GitHub-hosted
`ubuntu-latest` runners.

- `android-build.yml` runs on pushes to `main`, pull requests and manual
  dispatch. It runs unit tests, builds the debug APK, verifies the file exists
  and uploads the artifact.
- `android-release.yml` runs when a `v*` tag is pushed or manually dispatched.
  It builds the release APK and, for tags, publishes it as a GitHub Release
  asset.

## Download the APK from GitHub Actions

1. Open the repository's **Actions** tab.
2. Select the latest **Android Build** run.
3. Scroll to **Artifacts**.
4. Download `JNet-System-Monitor-APK`.
5. Extract the ZIP to get `app-debug.apk`.

## Download a released APK

1. Open the repository's **Releases** page.
2. Pick the latest version (for example `v1.0.0`).
3. Download `JNet-System-Monitor-v1.0.0-release-signed.apk` or the
   `debug-signed` variant if no release keystore was configured.

## Install the APK on Android

1. Copy the APK to the device or download it directly.
2. Open the file with the device file manager.
3. Allow installation from the file manager or browser when prompted.
4. Tap **Install**.
5. Launch **J~Net System Monitor**.

If an older build refuses to update, uninstall it first. This only happens when
the signing key changes. Future versions should be signed with the same release
keystore stored in the repository's Actions secrets so upgrades install over the
top.

## Trigger a manual build

1. Open the repository's **Actions** tab.
2. Choose **Android Build** or **Android Release**.
3. Click **Run workflow**.
4. Select the branch (`main`) and confirm.

## Release signing

The release workflow uses these repository secrets when present:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

When they are absent, the release APK is signed with the debug key and clearly
labelled `debug-signed` in the artifact and release notes. Keep the same keystore
for every release so users can update without uninstalling.

## Tests

Unit tests cover the formatting helpers used by the dashboard:

```bash
./gradlew testDebugUnitTest
```

They run automatically in the `Android Build` workflow.
