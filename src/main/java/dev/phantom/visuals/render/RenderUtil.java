package dev.phantom.visuals.render;

import net.minecraft.class_1921;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_765;

public final class RenderUtil {
    private RenderUtil() {}

    public static class_4588 lines(RenderCtx rc) {
        return rc.consumers.getBuffer(class_1921.method_23594());
    }

    /** Линия в мировых координатах (камера учитывается внутри). */
    public static void line(RenderCtx rc, class_4588 vc, class_243 a, class_243 b, int argb) {
        class_4587.class_4665 e = rc.matrices.method_23760();
        float ax = (float) (a.field_1352 - rc.cam.field_1352), ay = (float) (a.field_1351 - rc.cam.field_1351), az = (float) (a.field_1350 - rc.cam.field_1350);
        float bx = (float) (b.field_1352 - rc.cam.field_1352), by = (float) (b.field_1351 - rc.cam.field_1351), bz = (float) (b.field_1350 - rc.cam.field_1350);
        float dx = bx - ax, dy = by - ay, dz = bz - az;
        float len = class_3532.method_15355(dx * dx + dy * dy + dz * dz);
        if (len < 1.0E-6f) return;
        dx /= len;
        dy /= len;
        dz /= len;
        int al = (argb >>> 24) & 255, r = (argb >> 16) & 255, g = (argb >> 8) & 255, bl = argb & 255;
        vc.method_56824(e, ax, ay, az).method_1336(r, g, bl, al).method_60831(e, dx, dy, dz);
        vc.method_56824(e, bx, by, bz).method_1336(r, g, bl, al).method_60831(e, dx, dy, dz);
    }

    public static void box(RenderCtx rc, class_4588 vc, class_238 b, int argb) {
        class_243[] c = new class_243[8];
        int i = 0;
        for (int x = 0; x < 2; x++)
            for (int y = 0; y < 2; y++)
                for (int z = 0; z < 2; z++)
                    c[i++] = new class_243(x == 0 ? b.field_1323 : b.field_1320, y == 0 ? b.field_1322 : b.field_1325, z == 0 ? b.field_1321 : b.field_1324);
        for (int a = 0; a < 8; a++) {
            for (int bit = 0; bit < 3; bit++) {
                int o = a ^ (1 << bit);
                if (o > a) line(rc, vc, c[a], c[o], argb);
            }
        }
    }

    public static void cross(RenderCtx rc, class_4588 vc, class_243 p, double s, int argb) {
        line(rc, vc, p.method_1031(-s, 0, 0), p.method_1031(s, 0, 0), argb);
        line(rc, vc, p.method_1031(0, -s, 0), p.method_1031(0, s, 0), argb);
        line(rc, vc, p.method_1031(0, 0, -s), p.method_1031(0, 0, s), argb);
    }

    /** Текст, повёрнутый к камере, виден сквозь стены. */
    public static void text(RenderCtx rc, class_2561 text, class_243 pos, float scale, int color, int background) {
        class_4587 m = rc.matrices;
        m.method_22903();
        m.method_22904(pos.field_1352 - rc.cam.field_1352, pos.field_1351 - rc.cam.field_1351, pos.field_1350 - rc.cam.field_1350);
        m.method_22907(rc.camera.method_23767());
        m.method_22905(scale, -scale, scale);
        class_327 tr = rc.mc.field_1772;
        float w = tr.method_27525(text);
        tr.method_27522(text, -w / 2f, 0f, color, false, m.method_23760().method_23761(), rc.consumers,
                class_327.class_6415.field_33994, background, class_765.field_32767);
        m.method_22909();
    }
}
