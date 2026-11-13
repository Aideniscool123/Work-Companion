# Work Companion

A tiny animated desktop friend for Windows, built with Java Swing.

## Run in IntelliJ IDEA

Open this folder as a project and run `com.workcompanion.WorkCompanion` from `src/main/java`.

## Build the Windows app

Run `build-windows.ps1` in PowerShell. It compiles the app and uses the JDK's `jpackage` to create `dist/WorkCompanion/WorkCompanion.exe` with a bundled Java runtime. Java 17 or newer is required; the JDK must include `jpackage`.

The companion stays above other windows, can be dragged around the desktop, and gently bobs/blinks. Hover to open Settings. Character and size choices persist between launches.
