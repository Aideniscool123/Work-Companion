package com.workcompanion.ui;

import com.workcompanion.art.CharacterArtwork;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;

/** Owns the Windows notification-area icon and its context menu. */
public final class TrayController {
    private final CharacterArtwork artwork;
    private final Runnable openSettings;
    private final Runnable toggleVisibility;
    private final Runnable quit;
    private TrayIcon icon;

    public TrayController(CharacterArtwork artwork, Runnable openSettings,
                          Runnable toggleVisibility, Runnable quit) {
        this.artwork = artwork;
        this.openSettings = openSettings;
        this.toggleVisibility = toggleVisibility;
        this.quit = quit;
    }

    public void install() {
        if (!SystemTray.isSupported()) return;
        PopupMenu menu = new PopupMenu();
        MenuItem settings = new MenuItem("Settings");
        settings.addActionListener(event -> openSettings.run());
        MenuItem visibility = new MenuItem("Show / hide companion");
        visibility.addActionListener(event -> toggleVisibility.run());
        MenuItem exit = new MenuItem("Quit Work Companion");
        exit.addActionListener(event -> quit.run());
        menu.add(settings);
        menu.add(visibility);
        menu.addSeparator();
        menu.add(exit);

        icon = new TrayIcon(iconImage(0), "Work Companion", menu);
        icon.setImageAutoSize(true);
        icon.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1) openSettings.run();
            }
            @Override public void mousePressed(MouseEvent event) { }
            @Override public void mouseReleased(MouseEvent event) { }
            @Override public void mouseEntered(MouseEvent event) { }
            @Override public void mouseExited(MouseEvent event) { }
        });
        try {
            SystemTray.getSystemTray().add(icon);
        } catch (Exception exception) {
            System.err.println("Could not add notification-area icon: " + exception.getMessage());
        }
    }

    public void refreshIcon(int character) {
        if (icon == null) return;
        icon.setImage(iconImage(character));
        icon.setToolTip("Work Companion · " + com.workcompanion.art.CharacterCatalog
                .at(character).displayName());
    }

    public void remove() {
        if (icon != null) SystemTray.getSystemTray().remove(icon);
    }

    private Image iconImage(int character) {
        Image mascot = artwork.trayImage(character, 32);
        if (mascot != null) return mascot;
        BufferedImage fallback = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = fallback.createGraphics();
        graphics.setColor(new Color(111, 83, 144));
        graphics.fillOval(1, 1, 30, 30);
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Segoe UI", Font.BOLD, 17));
        graphics.drawString("W", 8, 23);
        graphics.dispose();
        return fallback;
    }
}
