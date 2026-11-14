package com.workcompanion.ui;

import com.workcompanion.platform.ThemeColors;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/** Flat, tactile button with a single rounded background. */
public final class RoundedButton extends JButton {
    private final ThemeColors theme;
    private final boolean primary;

    public RoundedButton(String label, ThemeColors theme, boolean primary) {
        super(label);
        this.theme = theme;
        this.primary = primary;
        setFont(new java.awt.Font("Segoe UI", primary ? java.awt.Font.BOLD : java.awt.Font.PLAIN, 12));
        setForeground(primary ? theme.accentText() : theme.foreground());
        setBorder(new EmptyBorder(9, 17, 9, 17));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = primary ? theme.accent() : theme.selectedSurface();
        if (getModel().isPressed()) fill = fill.darker();
        else if (getModel().isRollover()) fill = fill.brighter();
        g.setColor(fill);
        g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
        g.dispose();
        super.paintComponent(graphics);
    }
}
