package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.util.Binds;
import dev.phantom.visuals.util.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;

/** Список включённых модулей с их биндами справа сверху: "FreeCam [F6]". */
public class ModuleList extends Feature {
    private final BoolSetting showBinds = add(new BoolSetting("Show binds", true));
    private final BoolSetting showDisabled = add(new BoolSetting("Show disabled (with bind)", false));

    public ModuleList() {
        super("ModuleList", "On-screen list of enabled modules and their keybinds", Category.VISUALS, false);
    }

    private String label(Feature f) {
        return f.name + (showBinds.get() && f.bind > 0 ? " [" + Binds.name(f.bind) + "]" : "");
    }

    /** Вызывается из HudRenderCallback. */
    public void render(class_332 ctx) {
        if (!isEnabled()) return;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) return;
        class_327 font = mc.field_1772;

        List<Feature> list = new ArrayList<>();
        for (Feature f : ModuleManager.ALL) {
            if (f == this || f.isPermanent() || f.category == Category.CLIENT) continue;
            if (f.isEnabled() || (showDisabled.get() && f.bind > 0)) list.add(f);
        }
        list.sort((a, b) -> Integer.compare(font.method_1727(label(b)), font.method_1727(label(a))));

        int screenW = mc.method_22683().method_4486();
        int y = 4;
        for (int i = 0; i < list.size(); i++) {
            Feature f = list.get(i);
            String t = label(f);
            int w = font.method_1727(t) + 8;
            int x = screenW - w - 6;
            int accent = Theme.accent(i * 0.06f);
            ctx.method_25294(x, y, x + w, y + 11, 0x90101018);
            ctx.method_25294(x + w, y, x + w + 2, y + 11, accent);
            ctx.method_51433(font, t, x + 4, y + 2, f.isEnabled() ? accent : 0xFF777788, true);
            y += 11;
        }
    }
}
