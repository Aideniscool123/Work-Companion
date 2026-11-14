package com.workcompanion.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/** A single antialiased rounded surface used by the settings window. */
public final class RoundedPanel extends JPanel {
    private final Color fill;
    private final Color outline;
    private final int radius;

    public RoundedPanel(Color fill, Color outline, int radius) {
        this.fill = fill;
        this.outline = outline;
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(fill);
        g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1,
                radius, radius));
        g.setColor(outline);
        g.draw(new RoundRectangle2D.Double(.5, .5, getWidth() - 2, getHeight() - 2,
                radius, radius));
        g.dispose();
    }
}
