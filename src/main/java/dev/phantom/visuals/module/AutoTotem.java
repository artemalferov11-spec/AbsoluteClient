package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_746;

/** Держит тотем бессмертия в левой руке: после "попа" сам берёт новый из инвентаря. */
public class AutoTotem extends Feature {
    private final NumberSetting minHealth = add(new NumberSetting("Swap when HP <=", 1, 20, 1, 20));
    private final NumberSetting delay = add(new NumberSetting("Delay (ticks)", 0, 10, 1, 1));
    private final BoolSetting replace = add(new BoolSetting("Replace offhand item", true));
    private int cooldown;

    public AutoTotem() {
        super("AutoTotem", "Keeps a Totem of Undying in your offhand", Category.COMBAT, false);
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1761 == null) return;
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        if (p.field_7512 != p.field_7498) return; // открыт сундук/верстак и т.п.
        if (p.method_6079().method_31574(class_1802.field_8288)) return; // тотем уже в руке
        if (!replace.get() && !p.method_6079().method_7960()) return;
        if (p.method_6032() > minHealth.getI()) return;

        for (int i = 0; i < 36; i++) {
            if (p.method_31548().method_5438(i).method_31574(class_1802.field_8288)) {
                int slotId = i < 9 ? i + 36 : i;
                mc.field_1761.method_2906(p.field_7498.field_7763, slotId, 40, class_1713.field_7791, p);
                cooldown = delay.getI();
                return;
            }
        }
    }
}
