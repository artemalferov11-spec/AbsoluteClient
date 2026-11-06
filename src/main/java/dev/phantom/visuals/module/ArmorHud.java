package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import java.util.Locale;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_746;

/** Прочность брони в процентах рядом с хотбаром. */
public class ArmorHud extends Feature {
    private final BoolSetting total = add(new BoolSetting("Show average", true));
    private final NumberSetting offX = add(new NumberSetting("Offset X", -200, 200, 1, 0));
    private final NumberSetting offY = add(new NumberSetting("Offset Y", -100, 100, 1, 0));

    private static final class_1304[] SLOTS = {class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
    private static final String[] TAGS = {"H", "C", "L", "B"};

    public ArmorHud() {
        super("ArmorHud", "Armor durability in percent next to the hotbar", Category.VISUALS, false);
    }

    private static int color(int pct) {
        return pct > 66 ? 0xFF55FF55 : pct > 33 ? 0xFFFFFF55 : 0xFFFF5555;
    }

    /** Вызывается из HudRenderCallback. */
    public void render(class_332 ctx) {
        if (!isEnabled()) return;
        class_310 mc = class_310.method_1551();
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1755 != null) return;
        class_327 font = mc.field_1772;

        int screenW = mc.method_22683().method_4486();
        int screenH = mc.method_22683().method_4502();
        int x = screenW / 2 + 98 + offX.getI();           // справа от хотбара (хотбар заканчивается на +91)
        int y = screenH - 48 + offY.getI();

        int sum = 0, cnt = 0, row = 0;
        for (int i = 0; i < SLOTS.length; i++) {
            class_1799 st = p.method_6118(SLOTS[i]);
            if (st.method_7960()) continue;
            int pct = 100;
            if (st.method_7963()) pct = 100 - Math.round(100f * st.method_7919() / st.method_7936());
            sum += pct;
            cnt++;
            ctx.method_51433(font, TAGS[i] + " " + pct + "%", x, y + row * 10, color(pct), true);
            row++;
        }
        if (cnt > 0 && total.get()) {
            int avg = sum / cnt;
            ctx.method_51433(font, String.format(Locale.ROOT, "= %d%%", avg), x, y + row * 10, color(avg), true);
        }
    }
}
