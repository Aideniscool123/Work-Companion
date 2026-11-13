package com.workcompanion.platform;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/** Reads the user's Windows app theme preference without external libraries. */
public final class WindowsTheme {
    private WindowsTheme() { }

    public static ThemeColors detect() {
        if (!System.getProperty("os.name", "").toLowerCase().contains("win")) {
            return ThemeColors.forMode(false);
        }
        try {
            Process process = new ProcessBuilder("reg.exe", "query",
                    "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v", "AppsUseLightTheme").redirectErrorStream(true).start();
            String output;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().reduce("", (left, right) -> left + right);
            }
            process.waitFor();
            boolean dark = output.matches("(?s).*AppsUseLightTheme\\s+REG_DWORD\\s+0x0\\s*.*");
            return ThemeColors.forMode(dark);
        } catch (Exception ignored) {
            return ThemeColors.forMode(false);
        }
    }
}
