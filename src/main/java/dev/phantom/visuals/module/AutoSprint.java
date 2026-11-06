package dev.phantom.visuals.module;

import net.minecraft.class_310;
import net.minecraft.class_746;

public class AutoSprint extends Feature {
    public AutoSprint() {
        super("AutoSprint", "Sprint automatically while moving forward", Category.MOVEMENT, false);
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null) return;
        // В режиме критов спринт мешает (в воздухе со спринтом крита нет) — не включаем его, пока мы в прыжке
        if (ModuleManager.KILLAURA.critMode() && !p.method_24828()) return;
        if (mc.field_1690.field_1894.method_1434()
                && !p.method_5715()
                && !p.method_6115()
                && !p.field_5976
                && p.method_7344().method_7586() > 6) {
            p.method_5728(true);
        }
    }
}
