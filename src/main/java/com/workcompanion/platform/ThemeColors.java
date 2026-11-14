package com.workcompanion.platform;

import java.awt.Color;

/** Shared light and dark palettes for the companion UI. */
public record ThemeColors(boolean dark, Color background, Color surface, Color foreground,
                          Color secondaryText, Color border, Color accent,
                          Color accentText, Color selectedSurface) {
    public static ThemeColors forMode(boolean dark) {
        if (dark) {
            return new ThemeColors(true, new Color(30, 29, 36), new Color(42, 40, 49),
                    new Color(245, 242, 249), new Color(177, 171, 188),
                    new Color(68, 65, 78), new Color(129, 105, 158),
                    Color.WHITE, new Color(61, 53, 75));
        }
        return new ThemeColors(false, new Color(250, 249, 252), Color.WHITE,
                new Color(46, 43, 54), new Color(119, 113, 130),
                new Color(230, 226, 235), new Color(111, 83, 144),
                Color.WHITE, new Color(243, 237, 249));
    }
}
