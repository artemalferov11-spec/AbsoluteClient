package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;

public class ThemeFeature extends Feature {
    public final NumberSetting hue = add(new NumberSetting("Accent hue", 0, 360, 5, 265));
    public final BoolSetting rainbow = add(new BoolSetting("Rainbow", false));

    public ThemeFeature() {
        super("Theme", "Accent color of the GUI and effects", Category.CLIENT, true);
    }
}
