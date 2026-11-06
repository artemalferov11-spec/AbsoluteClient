package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.render.RenderUtil;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.ParticleStyles;
import dev.phantom.visuals.util.Theme;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1676;
import net.minecraft.class_243;
import net.minecraft.class_2394;
import net.minecraft.class_310;
import net.minecraft.class_4588;

public class Trails extends Feature {
    private final ModeSetting type = add(new ModeSetting("Type", "Ribbon", "Ribbon", "Particles", "Both"));
    private final ModeSetting style = add(new ModeSetting("Particle style", "End Rod", ParticleStyles.NAMES));
    private final BoolSetting self = add(new BoolSetting("Self", true));
    private final BoolSetting others = add(new BoolSetting("Other players", false));
    private final BoolSetting projectiles = add(new BoolSetting("Projectiles", true));
    private final NumberSetting density = add(new NumberSetting("Particle density", 1, 8, 1, 2));
    private final NumberSetting length = add(new NumberSetting("Ribbon length (ms)", 300, 4000, 100, 1400));
    private final NumberSetting height = add(new NumberSetting("Ribbon height", 0.1, 1.8, 0.1, 0.6));
    private final NumberSetting lines = add(new NumberSetting("Ribbon lines", 1, 8, 1, 4));

    /** История позиций для ленточных трейлов: {x, y, z, время в мс}. */
    private final Map<class_1297, ArrayDeque<double[]>> history = new IdentityHashMap<>();

    public Trails() {
        super("Trails", "Custom ribbon and particle trails behind players and projectiles", Category.PARTICLES, false);
    }

    @Override
    public void onDisable() {
        history.clear();
    }

    /** Подходит ли сущность под фильтры; возвращает смещение по Y или NaN. */
    private double yOffset(class_310 mc, class_1297 e) {
        if (e == mc.field_1724) return self.get() ? 0.1 : Double.NaN;
        if (e instanceof class_1657) return others.get() ? 0.1 : Double.NaN;
        if (e instanceof class_1676) return projectiles.get() ? 0.0 : Double.NaN;
        return Double.NaN;
    }

    @Override
    public void onTick(class_310 mc) {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            history.clear();
            return;
        }
        boolean ribbon = !type.is("Particles");
        boolean particles = !type.is("Ribbon") && !mc.method_1493();
        class_2394 effect = particles ? ParticleStyles.get(style.get()) : null;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        long now = System.currentTimeMillis();
        long keep = (long) length.get();
        Set<class_1297> seen = new HashSet<>();

        for (class_1297 e : mc.field_1687.method_18112()) {
            double yOff = yOffset(mc, e);
            if (Double.isNaN(yOff)) continue;
            seen.add(e);

            double dx = e.method_23317() - e.field_6014;
            double dy = e.method_23318() - e.field_6036;
            double dz = e.method_23321() - e.field_5969;
            boolean moved = dx * dx + dy * dy + dz * dz >= 0.0004;

            if (ribbon) {
                ArrayDeque<double[]> h = history.computeIfAbsent(e, k -> new ArrayDeque<>());
                if (moved) h.addLast(new double[]{e.method_23317(), e.method_23318() + yOff, e.method_23321(), now});
                while (!h.isEmpty() && now - (long) h.peekFirst()[3] > keep) h.pollFirst();
            }

            if (particles && moved) {
                for (int i = 0; i < density.getI(); i++) {
                    mc.field_1687.method_8406(effect,
                            e.method_23317() + (r.nextDouble() - 0.5) * 0.25,
                            e.method_23318() + yOff + r.nextDouble() * 0.15,
                            e.method_23321() + (r.nextDouble() - 0.5) * 0.25,
                            (r.nextDouble() - 0.5) * 0.02,
                            r.nextDouble() * 0.02,
                            (r.nextDouble() - 0.5) * 0.02);
                }
            }
        }
        // убрать историю исчезнувших сущностей
        for (Iterator<class_1297> it = history.keySet().iterator(); it.hasNext(); ) {
            if (!seen.contains(it.next())) it.remove();
        }
    }

    @Override
    public void onWorldRender(RenderCtx rc) {
        if (type.is("Particles") || history.isEmpty()) return;
        long now = System.currentTimeMillis();
        double span = length.get();
        int n = lines.getI();
        double h = height.get();
        class_4588 vc = RenderUtil.lines(rc);

        for (Map.Entry<class_1297, ArrayDeque<double[]>> en : history.entrySet()) {
            class_1297 e = en.getKey();
            ArrayDeque<double[]> dq = en.getValue();
            if (dq.isEmpty()) continue;
            double yOff = e instanceof class_1676 ? 0.0 : 0.1;
            double ribbonH = e instanceof class_1676 ? Math.min(h, 0.2) : h;

            // последняя точка — "живая" позиция, интерполированная по кадру
            double lx = e.field_6014 + (e.method_23317() - e.field_6014) * rc.delta;
            double ly = e.field_6036 + (e.method_23318() - e.field_6036) * rc.delta + yOff;
            double lz = e.field_5969 + (e.method_23321() - e.field_5969) * rc.delta;

            double[] prev = null;
            double prevT = 0;
            int idx = 0;
            for (double[] pt : dq) {
                double t = Math.min(1.0, (now - pt[3]) / span);        // 0 = свежая, 1 = старая
                if (prev != null) segment(rc, vc, prev, prevT, pt, t, n, ribbonH, idx);
                prev = pt;
                prevT = t;
                idx++;
            }
            if (prev != null) segment(rc, vc, prev, prevT, new double[]{lx, ly, lz, now}, 0.0, n, ribbonH, idx);
        }
    }

    /** Один отрезок ленты: n горизонтальных линий по высоте + вертикальная перемычка. */
    private void segment(RenderCtx rc, class_4588 vc, double[] a, double ta, double[] b, double tb,
                         int n, double ribbonH, int idx) {
        float fa = (float) Math.pow(1.0 - ta, 1.4);
        float fb = (float) Math.pow(1.0 - tb, 1.4);
        int ca = Theme.withAlpha(Theme.accent((float) ta * 0.6f), (int) (255 * fa));
        int cb = Theme.withAlpha(Theme.accent((float) tb * 0.6f), (int) (255 * fb));
        int mid = Theme.lerp(ca, cb, 0.5f);
        for (int k = 0; k < n; k++) {
            double off = n == 1 ? 0.0 : ribbonH * k / (n - 1);
            // у краёв ленты линии чуть прозрачнее — получается мягкое свечение
            int c = (n > 2 && (k == 0 || k == n - 1)) ? Theme.withAlpha(mid, ((mid >>> 24) & 255) * 2 / 3) : mid;
            RenderUtil.line(rc, vc, new class_243(a[0], a[1] + off, a[2]), new class_243(b[0], b[1] + off, b[2]), c);
        }
        if (n > 1 && (idx & 1) == 0) {
            RenderUtil.line(rc, vc, new class_243(b[0], b[1], b[2]), new class_243(b[0], b[1] + ribbonH, b[2]),
                    Theme.withAlpha(cb, ((cb >>> 24) & 255) / 2));
        }
    }
}
