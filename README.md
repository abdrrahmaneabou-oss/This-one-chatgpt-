# GuardBridge

Custom Android manager/server based on [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku).

## Behavior

- New client permission requests return **DENIED**, without an Allow popup.
- Select allowed apps manually in **Application management**.
- Press **Lock** to freeze permission additions and removals in the server.
- Lock also runs `settings put global development_settings_enabled 0` to hide Developer options. A failure to hide is reported separately; permissions remain locked.
- To edit again, keep a manager screen resumed, focused and full-screen for **60 uninterrupted seconds**. Backgrounding, losing window focus, screen lock, process restart, split screen, or a heartbeat interruption resets the countdown. Navigating between manager Activities also restarts the countdown.
- After the countdown, editing stays enabled until **Lock** is pressed again.
- The lock and selected permissions survive server restart. Countdown progress does not.
- The server never imports Android runtime permission grants from the official manager. The selected list is authoritative.
- Arbitrary shell/rish Binder request entrypoints are disabled. Supported Android apps receive the server Binder through their protected provider.

Package: `com.anas.guardbridge`. Android 7+; the APK is built for **arm64-v8a**, including the Red Magic 10S Pro.

## Download

Open **Actions → GuardBridge APK → latest successful run → GuardBridge-APK**. Extract the ZIP and install `guardbridge-release.apk`.

The APK uses a test signing certificate cached by GitHub Actions. Use a dedicated private release keystore for a long-term distribution; cache eviction can change the test certificate and prevent installing an update over an earlier build.

## Setup and verification

1. Stop/uninstall the official Shizuku to avoid running two competing servers.
2. Install GuardBridge, pair through Wireless debugging and start the service.
3. Open Application management and enable the desired apps manually.
4. Verify the allowed apps work and a non-allowed app cannot obtain permission through a popup.
5. Leave Android Settings before pressing **Lock**, because the OS Settings page may disable debugging when it observes Developer options being switched off while open.
6. Press **Lock** and verify Developer options disappear and allowed apps still work on the target device.
7. Leave the manager before 60 seconds; return and verify the timer starts from 60.
8. Stay for the entire minute; edit the list, then Lock again.
9. Restart the device and test service startup, paired-key persistence and the saved lock state.

Hiding Developer options is **not an OS access-control policy**. Build-number taps can expose it again. The separate protection app must block About phone/build-number routes; that project is not included here. OEM Settings behavior and client compatibility require testing on the actual phone. Shizuku-style ADB startup may be required again after reboot. This project does not promise absolute resistance to root, recovery, factory reset, or another already-authorized ADB client.

## Build

```sh
git clone --recurse-submodules https://github.com/abdrrahmaneabou-oss/This-one-chatgpt-.git
cd This-one-chatgpt-
./gradlew :manager:assembleRelease
```

Java 21, Android SDK 36, NDK 29.0.13113456, CMake 3.31.6. CI runs the monotonic timer tests before the Android build.

## Attribution

Derived from upstream commit `b844bc491f1790c72328e1a8e5b2349f8978f0ea`; API submodule `a27f6e4151ba7b39965ca47edb2bf0aeed7102e5`.
Upstream code is Apache-2.0 (see LICENSE). GuardBridge uses a new app name, application ID, manager permission, and original vector icon. Legacy client permission names and Binder keys are retained as compatibility identifiers, not declared as our runtime permissions.
