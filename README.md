# Work Companion

A tiny animated desktop friend for Windows, built with Java Swing.

## Run in IntelliJ IDEA

Open this folder as a project and run `com.workcompanion.WorkCompanion` from `src/main/java`.

## Build the Windows app

Run `build-windows.ps1` in PowerShell. It compiles the app and uses the JDK's `jpackage` to create `dist/WorkCompanion/WorkCompanion.exe` with a bundled Java runtime. Java 17 or newer is required; the JDK must include `jpackage`.

The companion stays above other windows, can be dragged within the usable desktop area, and plays a short hover reaction. It has no hover menu or drawn ground shadow. Click its notification-area icon to open the rounded character picker; right-click for Settings, show/hide, and Quit. The settings panel follows the Windows light/dark app preference and saves character and size choices. The packaged EXE registers itself to launch when the current Windows user signs in.

Three transparent characters and their idle/hover sprite sheets live under `src/main/resources/characters`. The static PNGs are 512 × 512; idle sheets contain four frames and hover sheets contain six, with 512 × 512 frame cells. Source art was supplied by the project owner.
