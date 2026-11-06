package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_746;

/** Баннер на экране со списком активных эффектов зелий. */
public class PotionHud extends Feature {
    private final NumberSetting posX = add(new NumberSetting("X", 0, 800, 2, 6));
    private final NumberSetting posY = add(new NumberSetting("Y", 0, 500, 2, 30));
    private final BoolSetting timer = add(new BoolSetting("Show timer", true));
    private final BoolSetting level = add(new BoolSetting("Show level", true));

    private static final String[] ROMAN = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

    public PotionHud() {
        super("PotionHud", "Banner with your active potion effects", Category.VISUALS, false);
    }

    private static String roman(int amp) {
        return amp >= 0 && amp < ROMAN.length ? ROMAN[amp] : String.valueOf(amp + 1);
    }

    private static String time(class_1293 e) {
        if (e.method_48559()) return "\u221E"; // бесконечный эффект
        int s = Math.max(0, e.method_5584() / 20);
        return (s / 60) + ":" + String.format("%02d", s % 60);
    }

    /** Вызывается из HudRenderCallback. */
    public void render(class_332 ctx) {
        if (!isEnabled()) return;
        class_310 mc = class_310.method_1551();
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1755 != null) return;

        List<class_1293> list = new ArrayList<>(p.method_6026());
        if (list.isEmpty()) return;
        // сверху — те, что дольше всего ещё будут действовать
        list.sort((a, b) -> Integer.compare(
                b.method_48559() ? Integer.MAX_VALUE : b.method_5584(),
                a.method_48559() ? Integer.MAX_VALUE : a.method_5584()));

        class_327 font = mc.field_1772;
        int rowH = 12, pad = 4;
        String[] names = new String[list.size()];
        String[] times = new String[list.size()];
        int nameW = 0, timeW = 0;
        for (int i = 0; i < list.size(); i++) {
            class_1293 e = list.get(i);
            class_1291 type = e.method_5579().comp_349();
            String n = type.method_5560().method_10851();
            if (level.get()) n += " " + roman(e.method_5578());
            names[i] = n;
            times[i] = timer.get() ? time(e) : "";
            nameW = Math.max(nameW, font.method_1727(n));
            timeW = Math.max(timeW, font.method_1727(times[i]));
        }

        int x = posX.getI(), y = posY.getI();
        int w = pad + 8 + 4 + nameW + (timer.get() ? 10 + timeW : 0) + pad;
        int h = pad + list.size() * rowH + pad - 2;

        ctx.method_25294(x, y, x + w, y + h, 0xB0121219);
        ctx.method_25294(x, y, x + 2, y + h, Theme.accent(0f));

        for (int i = 0; i < list.size(); i++) {
            class_1293 e = list.get(i);
            int ry = y + pad + i * rowH;
            int c = 0xFF000000 | e.method_5579().comp_349().method_5556();
            ctx.method_25294(x + pad + 2, ry + 1, x + pad + 2 + 8, ry + 9, c);
            ctx.method_51433(font, names[i], x + pad + 2 + 12, ry + 1, 0xFFFFFFFF, true);
            if (timer.get()) {
                boolean low = !e.method_48559() && e.method_5584() < 200; // < 10 сек
                ctx.method_51433(font, times[i], x + w - pad - timeW, ry + 1,
                        low ? 0xFFFF5555 : 0xFFB4B4CC, true);
            }
        }
    }
}
