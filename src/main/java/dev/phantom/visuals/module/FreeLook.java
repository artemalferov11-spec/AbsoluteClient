package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_5498;

public class FreeLook extends Feature {
    /** Состояние читают миксины камеры и мыши. */
    public static boolean active;
    public static float yaw;
    public static float pitch;

    private final ModeSetting mode = add(new ModeSetting("Mode", "Hold", "Hold", "Toggle"));
    private final BoolSetting thirdPerson = add(new BoolSetting("Third person", true));

    private boolean toggled;
    private class_5498 saved;

    public FreeLook() {
        super("FreeLook", "Look around without turning your character", Category.VISUALS, false);
    }

    /** Вызывается каждый тик с клавишей FreeLook. */
    public void tickKey(class_310 mc, class_304 key) {
        if (FreeCam.active) {
            while (key.method_1436()) { /* FreeCam управляет камерой */ }
            return;
        }
        boolean want;
        if (!isEnabled() || mc.field_1755 != null || mc.field_1724 == null) {
            while (key.method_1436()) { /* сбрасываем нажатия */ }
            toggled = false;
            want = false;
        } else if (mode.is("Hold")) {
            while (key.method_1436()) { /* сбрасываем нажатия */ }
            want = key.method_1434();
        } else {
            while (key.method_1436()) toggled = !toggled;
            want = toggled;
        }

        if (want && !active) start(mc);
        else if (!want && active) stop(mc);
    }

    private void start(class_310 mc) {
        active = true;
        yaw = mc.field_1724.method_36454();
        pitch = mc.field_1724.method_36455();
        if (thirdPerson.get()) {
            saved = mc.field_1690.method_31044();
            mc.field_1690.method_31043(class_5498.field_26665);
        }
    }

    private void stop(class_310 mc) {
        active = false;
        if (saved != null) {
            mc.field_1690.method_31043(saved);
            saved = null;
        }
    }

    @Override
    public void onDisable() {
        if (active) stop(class_310.method_1551());
        toggled = false;
    }
}
