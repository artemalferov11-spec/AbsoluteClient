package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.render.RenderUtil;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.Theme;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4588;

public class ESP extends Feature {
    private final ModeSetting mode = add(new ModeSetting("Mode", "Glow", "Glow", "Box", "Both"));
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting mobs = add(new BoolSetting("Mobs", false));
    private final BoolSetting throughWalls = add(new BoolSetting("Through walls", true));
    private final NumberSetting range = add(new NumberSetting("Range", 16, 256, 8, 128));

    public ESP() {
        super("ESP", "Highlights players / mobs through walls", Category.VISUALS, false);
    }

    private boolean accepts(class_1297 e) {
        if (!(e instanceof class_1309) || e instanceof class_1531) return false;
        if (e == class_310.method_1551().field_1724) return false;
        return e instanceof class_1657 ? players.get() : mobs.get();
    }

    /** Используется миксином Entity#isGlowing — контур виден сквозь стены. */
    public boolean glows(class_1297 e) {
        if (!isEnabled()) return false;
        // Box-линии рисуются с проверкой глубины (блоки их закрывают) — сквозь стены виден только контур Glow
        if (mode.is("Box") && !throughWalls.get()) return false;
        return accepts(e);
    }

    @Override
    public void onWorldRender(RenderCtx rc) {
        if (mode.is("Glow")) return;
        class_4588 vc = RenderUtil.lines(rc);
        for (class_1297 e : rc.mc.field_1687.method_18112()) {
            if (!accepts(e)) continue;
            class_243 p = e.method_30950(rc.delta);
            if (rc.cam.method_1022(p) > range.get()) continue;
            double hw = e.method_17681() / 2.0;
            class_238 box = new class_238(p.field_1352 - hw, p.field_1351, p.field_1350 - hw,
                    p.field_1352 + hw, p.field_1351 + e.method_17682(), p.field_1350 + hw);
            int col = e instanceof class_1657 ? Theme.accent(0f) : 0xFFFFAA00;
            RenderUtil.box(rc, vc, box, col);
        }
    }
}
