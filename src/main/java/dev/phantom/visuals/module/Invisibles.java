package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_310;

public class Invisibles extends Feature {
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting mobs = add(new BoolSetting("Mobs", true));

    public Invisibles() {
        super("Invisibles", "Outline for invisible players and mobs", Category.VISUALS, false);
    }

    /** Используется миксином Entity#isGlowing. */
    public boolean matches(class_1297 e) {
        if (!isEnabled()) return false;
        if (!(e instanceof class_1309) || e instanceof class_1531) return false;
        if (e == class_310.method_1551().field_1724) return false;
        if (!e.method_5767()) return false;
        return e instanceof class_1657 ? players.get() : mobs.get();
    }
}
