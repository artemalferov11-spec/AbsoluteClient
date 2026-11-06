package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.NumberSetting;
import net.minecraft.class_310;
import net.minecraft.class_746;

/**
 * Свободная камера: персонаж стоит на месте (см. ClientPlayerMixin), камера летает по WASD / Space / Shift.
 * Поворот камеры — через механизм FreeLook (мышь крутит камеру, а не игрока).
 */
public class FreeCam extends Feature {
    public static boolean active;
    private static double x, y, z;
    private static long last;
    private static final NumberSetting SPEED_REF[] = new NumberSetting[1];

    private final NumberSetting speed = add(new NumberSetting("Speed (blocks/s)", 2, 60, 1, 18));

    public FreeCam() {
        super("FreeCam", "Fly the camera around while your character stays put", Category.VISUALS, false);
        SPEED_REF[0] = speed;
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null) return;
        if (!active) start(mc, p);
    }

    private void start(class_310 mc, class_746 p) {
        net.minecraft.class_243 eye = p.method_5836(1.0f);
        x = eye.field_1352;
        y = eye.field_1351;
        z = eye.field_1350;
        last = System.nanoTime();
        FreeLook.yaw = p.method_36454();
        FreeLook.pitch = p.method_36455();
        FreeLook.active = true;
        active = true;
    }

    @Override
    public void onDisable() {
        if (active) {
            active = false;
            FreeLook.active = false;
        }
    }

    /** Вызывается миксином камеры каждый кадр. Возвращает {x, y, z} камеры. */
    public static double[] stepPos() {
        class_310 mc = class_310.method_1551();
        long now = System.nanoTime();
        double dt = Math.min(0.1, (now - last) / 1.0e9);
        last = now;
        if (mc.field_1755 == null) {
            double move = SPEED_REF[0].get() * dt;
            double yaw = Math.toRadians(FreeLook.yaw), pitch = Math.toRadians(FreeLook.pitch);
            double fx = -Math.sin(yaw) * Math.cos(pitch), fy = -Math.sin(pitch), fz = Math.cos(yaw) * Math.cos(pitch);
            double lx = Math.cos(yaw), lz = Math.sin(yaw);
            int f = (mc.field_1690.field_1894.method_1434() ? 1 : 0) - (mc.field_1690.field_1881.method_1434() ? 1 : 0);
            int s = (mc.field_1690.field_1913.method_1434() ? 1 : 0) - (mc.field_1690.field_1849.method_1434() ? 1 : 0);
            int v = (mc.field_1690.field_1903.method_1434() ? 1 : 0) - (mc.field_1690.field_1832.method_1434() ? 1 : 0);
            x += (fx * f + lx * s) * move;
            y += (fy * f + v) * move;
            z += (fz * f + lz * s) * move;
        }
        return new double[]{x, y, z};
    }
}
