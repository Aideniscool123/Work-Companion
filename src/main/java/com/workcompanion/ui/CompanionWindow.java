package com.workcompanion.ui;

import com.workcompanion.art.CharacterArtwork;
import com.workcompanion.platform.ScreenArea;

import javax.swing.JWindow;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/** Borderless, always-on-top character window with bounded drag movement. */
public final class CompanionWindow {
    private final CharacterArtwork artwork;
    private final JWindow window;
    private final CharacterCanvas canvas;
    private final Consumer<Point> locationSaved;
    private int character;
    private int size;
    private boolean visible = true;
    private Timer idleAnimation;
    private Timer hoverAnimation;
    private boolean hoverPlaying;
    private int idleFrame;
    private int hoverFrame;
    private Point dragOffset;

    public CompanionWindow(CharacterArtwork artwork, int character, int size,
                           Point saved, Consumer<Point> locationSaved) {
        this.artwork = artwork;
        this.character = character;
        this.size = size;
        this.locationSaved = locationSaved;
        window = new JWindow();
        window.setType(Window.Type.UTILITY);
        window.setAlwaysOnTop(true);
        window.setFocusableWindowState(false);
        window.setBackground(new Color(0, 0, 0, 0));
        canvas = new CharacterCanvas();
        window.setContentPane(canvas);
        window.setSize(size, size);
        Point start = saved.x < 0 ? defaultLocation(size) : saved;
        locate(start.x, start.y);
        installMouseBehavior();
        startIdleAnimation();
    }

    private Point defaultLocation(int size) {
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        return new Point(screen.width - size - 28, screen.height - size - 92);
    }

    private void installMouseBehavior() {
        canvas.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                dragOffset = event.getPoint();
            }
            @Override public void mouseReleased(MouseEvent event) {
                locationSaved.accept(window.getLocation());
            }
            @Override public void mouseEntered(MouseEvent event) { playHoverMotion(); }
        });
        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent event) {
                if (dragOffset == null) return;
                Point pointer = event.getLocationOnScreen();
                locate(pointer.x - dragOffset.x, pointer.y - dragOffset.y);
            }
        });
    }

    private void playHoverMotion() {
        if (hoverPlaying) return;
        stopIdleAnimation();
        hoverPlaying = true;
        hoverFrame = 0;
        int frameCount = Math.max(1, artwork.hoverFrameCount(character));
        hoverAnimation = new Timer(105, event -> {
            canvas.repaint();
            hoverFrame++;
            if (hoverFrame >= frameCount) {
                hoverPlaying = false;
                hoverFrame = 0;
                ((Timer) event.getSource()).stop();
                startIdleAnimation();
            }
        });
        hoverAnimation.start();
    }

    private void startIdleAnimation() {
        if (idleAnimation != null && idleAnimation.isRunning()) return;
        idleAnimation = new Timer(300, event -> {
            idleFrame++;
            canvas.repaint();
        });
        idleAnimation.start();
    }

    private void stopIdleAnimation() {
        if (idleAnimation != null) idleAnimation.stop();
    }

    private void locate(int x, int y) {
        var clamped = ScreenArea.clamp(x, y, window.getWidth(), window.getHeight());
        window.setLocation(clamped.x, clamped.y);
    }

    public void show() { window.setVisible(true); }
    public void close() {
        if (idleAnimation != null) idleAnimation.stop();
        if (hoverAnimation != null) hoverAnimation.stop();
        window.dispose();
    }
    public Window window() { return window; }
    public void setCharacter(int character) {
        this.character = character;
        idleFrame = 0;
        canvas.repaint();
    }
    public void setSize(int size) { this.size = size; window.setSize(size, size); locate(window.getX(), window.getY()); }
    public void toggleVisibility() { visible = !visible; window.setVisible(visible); }

    private final class CharacterCanvas extends JPanel {
        CharacterCanvas() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            BufferedImage image = hoverPlaying
                    ? artwork.hoverFrame(character, hoverFrame)
                    : artwork.idleFrame(character, idleFrame);
            if (image == null) image = artwork.image(character);
            if (image == null) return;
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            int margin = Math.max(4, size / 14);
            g.drawImage(image, margin, margin, size - margin * 2,
                    size - margin * 2, null);
            g.dispose();
        }
    }
}
