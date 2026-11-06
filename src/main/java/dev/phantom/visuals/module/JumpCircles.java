package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.render.RenderUtil;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_746;

/** Расходящиеся круги под ногами в момент прыжка. */
public class JumpCircles extends Feature {
    private final NumberSetting radius = add(new NumberSetting("Radius", 0.5, 4.0, 0.1, 1.6));
    private final NumberSetting duration = add(new NumberSetting("Duration (ms)", 300, 3000, 100, 900));

    private static final class Circle {
        final double x, y, z;
        final long start;
        Circle(double x, double y, double z, long start) {
            this.x = x; this.y = y; this.z = z; this.start = start;
        }
    }

    private final List<Circle> circles = new ArrayList<>();
    private boolean wasOnGround;

    public JumpCircles() {
        super("JumpCircles", "Expanding circles under your feet when you jump", Category.VISUALS, false);
    }

    @Override
    public void onDisable() {
        circles.clear();
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null) return;
        boolean ground = p.method_24828();
        if (wasOnGround && !ground && p.method_18798().field_1351 > 0.0) {
            circles.add(new Circle(p.method_23317(), p.method_23318() + 0.02, p.method_23321(), System.currentTimeMillis()));
        }
        wasOnGround = ground;
    }

    private void ring(RenderCtx rc, class_4588 vc, Circle c, double r, double rot, int color, int seg) {
        class_243 prev = null;
        for (int i = 0; i <= seg; i++) {
            double a = rot + i * (Math.PI * 2.0 / seg);
            class_243 cur = new class_243(c.x + Math.cos(a) * r, c.y, c.z + Math.sin(a) * r);
            if (prev != null) RenderUtil.line(rc, vc, prev, cur, color);
            prev = cur;
        }
    }

    @Override
    public void onWorldRender(RenderCtx rc) {
        if (circles.isEmpty()) return;
        long now = System.currentTimeMillis();
        long dur = (long) duration.get();
        class_4588 vc = RenderUtil.lines(rc);
        circles.removeIf(c -> now - c.start > dur);
        for (Circle c : circles) {
            float t = (now - c.start) / (float) dur;               // 0..1
            float ease = 1f - (float) Math.pow(1f - t, 3);          // быстро расходится, потом замедляется
            float fade = (1f - t) * (1f - t * 0.5f);
            double r = radius.get() * ease;
            double rot = t * 2.2;                                   // кольца медленно вращаются

            // мягкий "диск": несколько колец от центра к краю, прозрачность растёт к краю
            int rings = 5;
            for (int i = 1; i <= rings; i++) {
                float k = i / (float) rings;
                int a = (int) (255 * fade * (0.15f + 0.55f * k * k));
                ring(rc, vc, c, r * (0.35 + 0.65 * k), rot, Theme.withAlpha(Theme.accent(k * 0.5f), a), 48);
            }
            // яркий внешний обод (двойной — для толщины)
            int edge = Theme.withAlpha(Theme.accent(0.5f), (int) (255 * fade));
            ring(rc, vc, c, r, rot, edge, 64);
            ring(rc, vc, c, r * 1.04, rot, Theme.withAlpha(edge, ((edge >>> 24) & 255) / 2), 64);

            // лучи от центра к ободу
            int spokes = 8;
            for (int i = 0; i < spokes; i++) {
                double a = rot * 1.5 + i * (Math.PI * 2.0 / spokes);
                class_243 in = new class_243(c.x + Math.cos(a) * r * 0.35, c.y, c.z + Math.sin(a) * r * 0.35);
                class_243 out = new class_243(c.x + Math.cos(a) * r * 0.92, c.y, c.z + Math.sin(a) * r * 0.92);
                RenderUtil.line(rc, vc, in, out, Theme.withAlpha(Theme.accent(0.25f), (int) (170 * fade)));
            }
            // вторая, поднимающаяся вверх волна
            double rise = 0.05 + 0.5 * ease;
            class_243 up = new class_243(c.x, c.y + rise, c.z);
            Circle lifted = new Circle(up.field_1352, up.field_1351, up.field_1350, c.start);
            ring(rc, vc, lifted, r * 0.55, -rot, Theme.withAlpha(Theme.accent(0.8f), (int) (140 * fade)), 40);
        }
    }
}
