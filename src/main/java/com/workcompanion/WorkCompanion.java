package com.workcompanion;

import com.workcompanion.art.CharacterArtwork;
import com.workcompanion.art.CharacterCatalog;
import com.workcompanion.platform.StartupRegistration;
import com.workcompanion.platform.WindowsTheme;
import com.workcompanion.ui.CompanionWindow;
import com.workcompanion.ui.SettingsWindow;
import com.workcompanion.ui.TrayController;

import javax.swing.SwingUtilities;
import java.awt.Point;
import java.util.prefs.Preferences;

/** Application entry point and owner of persistent companion settings. */
public final class WorkCompanion {
    private final Preferences preferences = Preferences.userNodeForPackage(WorkCompanion.class);
    private CharacterArtwork artwork;
    private CompanionWindow companion;
    private TrayController tray;
    private SettingsWindow settings;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WorkCompanion().start());
    }

    private void start() {
        StartupRegistration.registerCurrentExecutable();
        artwork = new CharacterArtwork();
        Point savedLocation = new Point(preferences.getInt("x", -1), preferences.getInt("y", -1));
        int character = CharacterCatalog.at(preferences.getInt("character", 0)).ordinal();
        int size = Math.max(80, Math.min(160, preferences.getInt("size", 112)));

        companion = new CompanionWindow(artwork, character, size, savedLocation, this::saveLocation);
        tray = new TrayController(artwork, this::openSettings,
                companion::toggleVisibility, this::quit);
        companion.show();
        tray.install();
    }

    private void openSettings() {
        if (settings != null && settings.isDisplayable()) {
            settings.toFront();
            return;
        }
        settings = new SettingsWindow(companion.window(),
                artwork, WindowsTheme.detect(),
                preferences.getInt("character", 0), preferences.getInt("size", 112),
                (character, size) -> {
                    preferences.putInt("character", character);
                    preferences.putInt("size", size);
                    companion.setCharacter(character);
                    companion.setSize(size);
                    tray.refreshIcon(character);
                });
        settings.setVisible(true);
    }

    private void saveLocation(Point location) {
        preferences.putInt("x", location.x);
        preferences.putInt("y", location.y);
    }

    private void quit() {
        if (tray != null) tray.remove();
        if (companion != null) companion.close();
        System.exit(0);
    }
}
