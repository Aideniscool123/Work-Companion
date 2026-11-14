package com.workcompanion.art;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/** Loads character portraits and animation sheets bundled in resources/characters. */
public final class CharacterArtwork {
    private final Map<CharacterCatalog, CharacterImages> images =
            new EnumMap<>(CharacterCatalog.class);

    public CharacterArtwork() {
        for (CharacterCatalog character : CharacterCatalog.values()) {
            try {
                BufferedImage portrait = read(character.portraitName());
                BufferedImage idle = read(character.idleSheetName());
                BufferedImage hover = read(character.hoverSheetName());
                images.put(character, new CharacterImages(portrait, idle, hover));
            } catch (IOException exception) {
                System.err.println("Could not load art for " + character.displayName()
                        + ": " + exception.getMessage());
            }
        }
    }

    public BufferedImage image(int index) {
        CharacterImages set = images.get(CharacterCatalog.at(index));
        return set == null ? null : set.portrait();
    }

    public BufferedImage idleFrame(int index, int frame) {
        CharacterImages set = images.get(CharacterCatalog.at(index));
        return set == null ? null : frame(set.idle(), frame);
    }

    public BufferedImage hoverFrame(int index, int frame) {
        CharacterImages set = images.get(CharacterCatalog.at(index));
        return set == null ? null : frame(set.hover(), frame);
    }

    public int idleFrameCount(int index) {
        CharacterImages set = images.get(CharacterCatalog.at(index));
        return set == null ? 0 : set.idle().getWidth() / set.idle().getHeight();
    }

    public int hoverFrameCount(int index) {
        CharacterImages set = images.get(CharacterCatalog.at(index));
        return set == null ? 0 : set.hover().getWidth() / set.hover().getHeight();
    }

    public Image trayImage(int index, int size) {
        BufferedImage image = image(index);
        return image == null ? null : image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
    }

    public boolean hasCompleteCast() {
        return images.size() == CharacterCatalog.values().length
                && images.values().stream().allMatch(set -> set.portrait() != null
                && set.idle() != null && set.hover() != null);
    }

    private BufferedImage read(String name) throws IOException {
        try (var stream = getClass().getResourceAsStream("/characters/" + name)) {
            if (stream == null) throw new IOException("missing resource " + name);
            return ImageIO.read(stream);
        }
    }

    private BufferedImage frame(BufferedImage sheet, int index) {
        int frameSize = sheet.getHeight();
        int frameCount = sheet.getWidth() / frameSize;
        int frameIndex = Math.floorMod(index, frameCount);
        return sheet.getSubimage(frameIndex * frameSize, 0, frameSize, frameSize);
    }

    private record CharacterImages(BufferedImage portrait,
                                   BufferedImage idle, BufferedImage hover) { }
}
