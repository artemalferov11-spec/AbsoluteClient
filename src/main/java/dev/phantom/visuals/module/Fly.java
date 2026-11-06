package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.MoveUtil;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class Fly extends Feature {
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.1, 3.0, 0.05, 0.6));
    private final NumberSetting vertical = add(new NumberSetting("Vertical speed", 0.1, 2.0, 0.05, 0.5));
    private final BoolSetting antiKick = add(new BoolSetting("Anti-kick", true));
    private int ticks;

    public Fly() {
        super("Fly", "Fly freely: Space = up, Shift = down", Category.MOVEMENT, false);
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null) return;
        if (mc.field_1755 != null) {
            p.method_18800(0, 0, 0);
            return;
        }
        double[] w = MoveUtil.wish(mc, p);
        double vx = w == null ? 0 : w[0] * speed.get();
        double vz = w == null ? 0 : w[1] * speed.get();
        double vy = 0;
        if (mc.field_1690.field_1903.method_1434()) vy += vertical.get();
        if (mc.field_1690.field_1832.method_1434()) vy -= vertical.get();

        // Сервер кикает за "Flying is not enabled", если долго висишь на месте — чуть проседаем раз в секунду
        ticks++;
        if (antiKick.get() && vy == 0 && ticks % 20 == 0) vy = -0.04;

        p.method_18800(vx, vy, vz);
        p.field_6017 = 0f;
    }
}
