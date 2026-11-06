package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.MoveUtil;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class Speed extends Feature {
    private final ModeSetting mode = add(new ModeSetting("Mode", "Bhop", "Ground", "Bhop"));
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.1, 1.0, 0.01, 0.32));

    public Speed() {
        super("Speed", "Move faster (Ground = run faster, Bhop = auto-jump + speed)", Category.MOVEMENT, false);
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1755 != null) return;
        double[] w = MoveUtil.wish(mc, p);
        if (w == null) return;
        if (p.method_5799() || p.method_5771() || p.method_6101()) return; // вода / лава / лестница — не трогаем

        if (mode.is("Bhop") && p.method_24828()) {
            class_243 v = p.method_18798();
            p.method_18800(v.field_1352, 0.42, v.field_1350);
        }
        if (p.method_24828() || mode.is("Bhop")) {
            MoveUtil.setH(p, w[0] * speed.get(), w[1] * speed.get());
        }
    }
}
