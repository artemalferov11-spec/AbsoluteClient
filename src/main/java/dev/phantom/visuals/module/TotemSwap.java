package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class TotemSwap extends Feature {
    private final BoolSetting totemFallback = add(new BoolSetting("Totem <-> Totem", true));

    public TotemSwap() {
        super("TotemSwap", "Key X: swaps offhand totem <-> player head", Category.UTILITY, false);
    }

    /** Вызывается по нажатию клавиши. */
    public void run(class_310 mc) {
        if (!isEnabled()) return;
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1761 == null) return;
        if (p.field_7512 != p.field_7498) return;

        if (p.method_6079().method_31574(class_1802.field_8288)) {
            if (trySwap(mc, p, class_1802.field_8575)) return;
            if (totemFallback.get() && trySwap(mc, p, class_1802.field_8288)) return;
            p.method_7353(class_2561.method_43471("message.phantomvisuals.no_head"), true);
        } else {
            if (trySwap(mc, p, class_1802.field_8288)) return;
            p.method_7353(class_2561.method_43471("message.phantomvisuals.no_totem"), true);
        }
    }

    private boolean trySwap(class_310 mc, class_746 p, class_1792 target) {
        for (int i = 0; i < 36; i++) {
            if (p.method_31548().method_5438(i).method_31574(target)) {
                int slotId = i < 9 ? i + 36 : i;
                mc.field_1761.method_2906(p.field_7498.field_7763, slotId, 40, class_1713.field_7791, p);
                return true;
            }
        }
        return false;
    }
}
