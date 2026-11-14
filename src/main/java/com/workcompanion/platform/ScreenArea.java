package com.workcompanion.platform;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

/** Clamps the companion inside a monitor's usable area, clear of its taskbar. */
public final class ScreenArea {
    private static final int EDGE_GAP = 10;

    private ScreenArea() { }

    public static Rectangle clamp(int x, int y, int width, int height) {
        Rectangle bounds = monitorFor(x, y);
        int maxX = Math.max(bounds.x, bounds.x + bounds.width - width);
        int maxY = Math.max(bounds.y, bounds.y + bounds.height - height);
        return new Rectangle(Math.max(bounds.x, Math.min(x, maxX)),
                Math.max(bounds.y, Math.min(y, maxY)), width, height);
    }

    private static Rectangle monitorFor(int x, int y) {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice device : environment.getScreenDevices()) {
            Rectangle bounds = device.getDefaultConfiguration().getBounds();
            if (bounds.contains(x, y)) return usable(device, bounds);
        }
        GraphicsDevice primary = environment.getDefaultScreenDevice();
        return usable(primary, primary.getDefaultConfiguration().getBounds());
    }

    private static Rectangle usable(GraphicsDevice device, Rectangle bounds) {
        Insets insets = Toolkit.getDefaultToolkit()
                .getScreenInsets(device.getDefaultConfiguration());
        return new Rectangle(bounds.x + insets.left + EDGE_GAP,
                bounds.y + insets.top + EDGE_GAP,
                Math.max(1, bounds.width - insets.left - insets.right - EDGE_GAP * 2),
                Math.max(1, bounds.height - insets.top - insets.bottom - EDGE_GAP * 2));
    }
}
