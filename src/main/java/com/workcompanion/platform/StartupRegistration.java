package com.workcompanion.platform;

import java.nio.file.Path;

/** Registers the packaged EXE in the current user's Windows startup applications. */
public final class StartupRegistration {
    private static final String RUN_KEY = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";

    private StartupRegistration() { }

    public static void registerCurrentExecutable() {
        if (!System.getProperty("os.name", "").toLowerCase().contains("win")) return;
        String executable = System.getProperty("jpackage.app-path");
        if (executable == null || executable.isBlank()) return;
        try {
            String command = '"' + Path.of(executable).toAbsolutePath().toString() + '"';
            Process process = new ProcessBuilder("reg.exe", "add", RUN_KEY,
                    "/v", "WorkCompanion", "/t", "REG_SZ", "/d", command, "/f")
                    .redirectErrorStream(true).start();
            process.waitFor();
            if (process.exitValue() != 0) {
                System.err.println("Could not register Work Companion for startup.");
            }
        } catch (Exception exception) {
            System.err.println("Startup registration failed: " + exception.getMessage());
        }
    }
}
