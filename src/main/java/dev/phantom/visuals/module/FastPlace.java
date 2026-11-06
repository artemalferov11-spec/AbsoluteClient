package dev.phantom.visuals.module;

import dev.phantom.visuals.mixin.MinecraftClientAccessor;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import net.minecraft.class_1747;
import net.minecraft.class_310;

public class FastPlace extends Feature {
    private final BoolSetting blocksOnly = add(new BoolSetting("Blocks only", true));
    private final NumberSetting delay = add(new NumberSetting("Delay (ticks)", 0, 4, 1, 0));

    public FastPlace() {
        super("FastPlace", "Removes the delay between placing blocks", Category.UTILITY, false);
    }

    @Override
    public void onTick(class_310 mc) {
        if (mc.field_1724 == null) return;
        if (blocksOnly.get()
                && !(mc.field_1724.method_6047().method_7909() instanceof class_1747)
                && !(mc.field_1724.method_6079().method_7909() instanceof class_1747)) {
            return;
        }
        MinecraftClientAccessor acc = (MinecraftClientAccessor) (Object) mc;
        if (acc.phantom$getItemUseCooldown() > delay.getI()) {
            acc.phantom$setItemUseCooldown(delay.getI());
        }
    }
}
