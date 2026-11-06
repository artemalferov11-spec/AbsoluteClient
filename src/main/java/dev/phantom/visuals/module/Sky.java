package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import net.minecraft.class_243;
import net.minecraft.class_3532;

/** Свой цвет неба (только у тебя на экране). Читается миксином ClientWorldMixin. */
public class Sky extends Feature {
    private final ModeSetting mode = add(new ModeSetting("Mode", "Custom", "Custom", "Rainbow", "Theme"));
    private final NumberSetting hue = add(new NumberSetting("Hue", 0, 360, 5, 280));
    private final NumberSetting sat = add(new NumberSetting("Saturation", 0, 100, 5, 60));
    private final NumberSetting bri = add(new NumberSetting("Brightness", 0, 100, 5, 85));

    public Sky() {
        super("Sky", "Change the color of the sky", Category.VISUALS, false);
    }

    public class_243 color() {
        float h;
        switch (mode.get()) {
            case "Rainbow":
                h = (System.currentTimeMillis() % 10000L) / 10000f;
                break;
            case "Theme":
                h = ModuleManager.THEME.rainbow.get()
                        ? (System.currentTimeMillis() % 8000L) / 8000f
                        : ModuleManager.THEME.hue.getF() / 360f;
                break;
            default:
                h = hue.getF() / 360f;
        }
        int rgb = class_3532.method_15369(h, sat.getF() / 100f, bri.getF() / 100f);
        return new class_243(((rgb >> 16) & 255) / 255.0, ((rgb >> 8) & 255) / 255.0, (rgb & 255) / 255.0);
    }
}
