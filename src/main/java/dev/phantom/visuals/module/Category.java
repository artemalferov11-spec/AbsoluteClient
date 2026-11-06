package dev.phantom.visuals.module;

public enum Category {
    VISUALS("Visuals"),
    PARTICLES("Particles"),
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    UTILITY("Utility"),
    CLIENT("Client");

    public final String label;

    Category(String label) {
        this.label = label;
    }
}
