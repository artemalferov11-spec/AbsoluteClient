package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.MoveUtil;
import net.minecraft.class_310;
import net.minecraft.class_746;

/** Управление в воздухе: скорость сохраняется и направляется туда, куда жмёшь WASD. */
public class Strafe extends Feature {
    private final NumberSetting minSpeed = add(new NumberSetting("Min speed", 0.1, 0.6, 0.01, 0.22));
    private final BoolSetting onGround = add(new BoolSetting("Also on ground", false));

    public Strafe() {
        super("Strafe", "Full air control: keeps speed and turns it where you steer", Category.MOVEMENT, false);
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1755 != null) return;
        if (p.method_24828() && !onGround.get()) return;
        double[] w = MoveUtil.wish(mc, p);
        if (w == null) return;
        double sp = Math.max(MoveUtil.hSpeed(p), minSpeed.get());
        MoveUtil.setH(p, w[0] * sp, w[1] * sp);
    }
}
