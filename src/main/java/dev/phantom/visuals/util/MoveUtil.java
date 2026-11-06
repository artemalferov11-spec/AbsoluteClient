package dev.phantom.visuals.util;

import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;

public final class MoveUtil {
    private MoveUtil() {}

    /** Направление, куда игрок хочет идти (по зажатым WASD и повороту головы), длина 1. null — клавиши не нажаты. */
    public static double[] wish(class_310 mc, class_746 p) {
        int f = (mc.field_1690.field_1894.method_1434() ? 1 : 0) - (mc.field_1690.field_1881.method_1434() ? 1 : 0);
        int s = (mc.field_1690.field_1913.method_1434() ? 1 : 0) - (mc.field_1690.field_1849.method_1434() ? 1 : 0); // +1 = влево
        if (f == 0 && s == 0) return null;
        double yaw = Math.toRadians(p.method_36454());
        double fx = -Math.sin(yaw), fz = Math.cos(yaw);   // вперёд
        double lx = fz, lz = -fx;                          // влево
        double x = fx * f + lx * s, z = fz * f + lz * s;
        double len = Math.sqrt(x * x + z * z);
        return new double[]{x / len, z / len};
    }

    public static double hSpeed(class_746 p) {
        class_243 v = p.method_18798();
        return Math.sqrt(v.field_1352 * v.field_1352 + v.field_1350 * v.field_1350);
    }

    public static void setH(class_746 p, double x, double z) {
        p.method_18800(x, p.method_18798().field_1351, z);
    }
}
