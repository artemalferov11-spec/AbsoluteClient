package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class Spider extends Feature {
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.05, 0.6, 0.01, 0.2));
    private final BoolSetting needForward = add(new BoolSetting("Only while moving forward", true));

    public Spider() {
        super("Spider", "Climb up walls you are pushing against", Category.MOVEMENT, false);
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1755 != null) return;
        if (!p.field_5976) return; // нет столкновения со стеной
        if (needForward.get() && !mc.field_1690.field_1894.method_1434()) return;
        class_243 v = p.method_18798();
        p.method_18800(v.field_1352, speed.get(), v.field_1350);
    }
}
