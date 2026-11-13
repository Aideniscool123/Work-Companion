package com.workcompanion.art;

/** Display names and PNG resource paths for the selected companion cast. */
public enum CharacterCatalog {
    ORBIT("Orbit", "orbit.png", "orbit-idle.png", "orbit-hover.png"),
    PUDDLE("Puddle", "puddle.png", "puddle-idle.png", "puddle-hover.png"),
    PUFF("Puff", "puff.png", "puff-idle.png", "puff-hover.png");

    private final String displayName;
    private final String portraitName;
    private final String idleSheetName;
    private final String hoverSheetName;

    CharacterCatalog(String displayName, String portraitName,
                     String idleSheetName, String hoverSheetName) {
        this.displayName = displayName;
        this.portraitName = portraitName;
        this.idleSheetName = idleSheetName;
        this.hoverSheetName = hoverSheetName;
    }

    public String displayName() { return displayName; }
    public String portraitName() { return portraitName; }
    public String idleSheetName() { return idleSheetName; }
    public String hoverSheetName() { return hoverSheetName; }

    public static CharacterCatalog at(int index) {
        CharacterCatalog[] values = values();
        return values[Math.floorMod(index, values.length)];
    }
}
