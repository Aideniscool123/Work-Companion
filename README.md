# Work Companion

A tiny animated desktop friend for Windows, built with Java Swing.

## Run in IntelliJ IDEA

Open this folder as a project and run `com.workcompanion.WorkCompanion` from `src/main/java`.

## Build the Windows app

Run `build-windows.ps1` in PowerShell. It compiles the app and uses the JDK's `jpackage` to create `dist/WorkCompanion/WorkCompanion.exe` with a bundled Java runtime. Java 17 or newer is required; the JDK must include `jpackage`.

The companion stays above other windows, can be dragged around the desktop, and gently bobs, blinks, and sways. The ten original illustrated friends each have their own silhouette and details. Hover for quick Settings. The notification-area icon offers Settings, show/hide, and Quit, keeping the app available without a taskbar button. The rounded settings panel follows the Windows app light/dark preference and saves character and size choices between launches.
