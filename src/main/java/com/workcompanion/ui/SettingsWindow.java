package com.workcompanion.ui;

import com.workcompanion.art.CharacterArtwork;
import com.workcompanion.art.CharacterCatalog;
import com.workcompanion.platform.ThemeColors;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/** Character picker and size control shown when the tray icon is clicked. */
public final class SettingsWindow extends JFrame {
    private final ThemeColors theme;
    private final CharacterArtwork artwork;
    private final SaveAction saveAction;
    private int selected;
    private int currentSize;
    private PointOffset dragOffset;
    private CharacterCard[] cards;

    public SettingsWindow(Window owner, CharacterArtwork artwork, ThemeColors theme,
                          int character, int size, SaveAction saveAction) {
        super("Work Companion");
        this.theme = theme;
        this.artwork = artwork;
        this.selected = character;
        this.currentSize = size;
        this.saveAction = saveAction;
        buildUi();
        pack();
        setLocationRelativeTo(owner);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setBackground(new java.awt.Color(0, 0, 0, 0));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void buildUi() {
        RoundedPanel root = new RoundedPanel(theme.surface(), theme.border(), 24);
        root.setLayout(new BorderLayout(0, 0));
        root.setBorder(new EmptyBorder(18, 19, 16, 19));
        root.add(header(), BorderLayout.NORTH);
        root.add(characterGrid(), BorderLayout.CENTER);
        root.add(footer(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel header() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new javax.swing.BoxLayout(labels, javax.swing.BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Your desktop companion");
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        title.setForeground(theme.foreground());
        JLabel subtitle = new JLabel("Choose a friend and make them feel at home.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(theme.secondaryText());
        labels.add(title);
        labels.add(javax.swing.Box.createVerticalStrut(4));
        labels.add(subtitle);
        RoundedButton close = new RoundedButton("×", theme, false);
        close.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        close.addActionListener(event -> dispose());
        header.add(labels, BorderLayout.CENTER);
        header.add(close, BorderLayout.EAST);
        header.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                dragOffset = new PointOffset(event.getX(), event.getY());
            }
        });
        header.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent event) {
                if (dragOffset != null) setLocation(event.getXOnScreen() - dragOffset.x,
                        event.getYOnScreen() - dragOffset.y);
            }
        });
        return header;
    }

    private JPanel characterGrid() {
        JPanel grid = new JPanel(new java.awt.GridLayout(1, 3, 9, 0));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(20, 0, 18, 0));
        ButtonGroup group = new ButtonGroup();
        cards = new CharacterCard[CharacterCatalog.values().length];
        for (int index = 0; index < cards.length; index++) {
            CharacterCard card = new CharacterCard(index);
            cards[index] = card;
            group.add(card);
            grid.add(card);
        }
        cards[selected].setSelected(true);
        return grid;
    }

    private JPanel footer() {
        JPanel footer = new JPanel(new BorderLayout(0, 6));
        footer.setOpaque(false);
        JPanel sliderLabels = new JPanel(new BorderLayout());
        sliderLabels.setOpaque(false);
        JLabel sizeLabel = new JLabel("Character size");
        sizeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sizeLabel.setForeground(theme.foreground());
        JLabel valueLabel = new JLabel(currentSize + " px");
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueLabel.setForeground(theme.secondaryText());
        sliderLabels.add(sizeLabel, BorderLayout.WEST);
        sliderLabels.add(valueLabel, BorderLayout.EAST);
        JSlider slider = new JSlider(80, 160, currentSize);
        slider.setOpaque(false);
        slider.setForeground(theme.accent());
        slider.addChangeListener(event -> {
            currentSize = slider.getValue();
            valueLabel.setText(currentSize + " px");
        });
        JLabel themeNote = new JLabel(theme.dark() ? "Using your Windows dark theme"
                : "Using your Windows light theme");
        themeNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        themeNote.setForeground(theme.secondaryText());
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(themeNote, BorderLayout.WEST);
        RoundedButton done = new RoundedButton("Save changes", theme, true);
        done.addActionListener(event -> {
            saveAction.save(selected, currentSize);
            dispose();
        });
        bottom.add(done, BorderLayout.EAST);
        footer.add(sliderLabels, BorderLayout.NORTH);
        footer.add(slider, BorderLayout.CENTER);
        footer.add(bottom, BorderLayout.SOUTH);
        return footer;
    }

    @FunctionalInterface
    public interface SaveAction { void save(int character, int size); }

    private record PointOffset(int x, int y) { }

    private final class CharacterCard extends JToggleButton {
        private final int index;

        CharacterCard(int index) {
            this.index = index;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(126, 116));
            addActionListener(event -> {
                selected = this.index;
                for (CharacterCard card : cards) card.repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean active = isSelected();
            g.setColor(active ? theme.selectedSurface() : theme.background());
            g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
            g.setColor(active ? theme.accent() : theme.border());
            g.draw(new RoundRectangle2D.Double(.5, .5, getWidth() - 1, getHeight() - 1, 15, 15));
            BufferedImage sprite = artwork.image(index);
            if (sprite != null) {
                Image thumbnail = sprite.getScaledInstance(78, 78, Image.SCALE_SMOOTH);
                g.drawImage(thumbnail, (getWidth() - 78) / 2, 6, null);
            }
            g.setColor(theme.foreground());
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            String name = CharacterCatalog.at(index).displayName();
            int textX = (getWidth() - g.getFontMetrics().stringWidth(name)) / 2;
            g.drawString(name, textX, getHeight() - 13);
            g.dispose();
        }
    }
}
