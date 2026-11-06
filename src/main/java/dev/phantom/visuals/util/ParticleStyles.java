package dev.phantom.visuals.util;

import net.minecraft.class_2394;
import net.minecraft.class_2398;

public final class ParticleStyles {
    private ParticleStyles() {}

    public static final String[] NAMES = {
            "Flame", "Soul Fire", "End Rod", "Snowflake", "Heart", "Enchant",
            "Electric", "Glow", "Firework", "Crit", "Dragon", "Totem", "Note"
    };

    public static class_2394 get(String name) {
        return switch (name) {
            case "Flame" -> class_2398.field_11240;
            case "Soul Fire" -> class_2398.field_22246;
            case "Snowflake" -> class_2398.field_28013;
            case "Heart" -> class_2398.field_11201;
            case "Enchant" -> class_2398.field_11215;
            case "Electric" -> class_2398.field_29644;
            case "Glow" -> class_2398.field_28479;
            case "Firework" -> class_2398.field_11248;
            case "Crit" -> class_2398.field_11205;
            case "Dragon" -> class_2398.field_11216;
            case "Totem" -> class_2398.field_11220;
            case "Note" -> class_2398.field_11224;
            default -> class_2398.field_11207;
        };
    }
}
