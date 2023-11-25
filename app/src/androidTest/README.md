# Test

## Instrumented Tests
Instrumented tests are located at $module-name/src/androidTest/java/. These tests run on a hardware
device or emulator. They have access to Instrumentation APIs that give you access to information,
such as the Context class, on the app you are testing, and let you control the app under test from
your test code. Instrumented tests are built into a separate APK, so they have their own
AndroidManifest.xml file. This file is generated automatically, but you can create your own version
at $module-name/src/androidTest/AndroidManifest.xml, which will be merged with the generated
manifest. Use instrumented tests when writing integration and functional UI tests to automate user
interaction, or when your tests have Android dependencies that you can’t create test doubles for.
For more information on how to write instrumented tests, see Build instrumented tests and Automate
UI tests.

## References
- [test-in-android-studio](https://developer.android.com/studio/test/test-in-android-studio)
