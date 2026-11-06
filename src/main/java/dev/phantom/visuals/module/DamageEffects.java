package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.render.RenderUtil;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.ParticleStyles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_2394;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_310;

public class DamageEffects extends Feature {
    private final ModeSetting style = add(new ModeSetting("Style", "Crit", ParticleStyles.NAMES));
    private final NumberSetting amount = add(new NumberSetting("Particles", 4, 80, 2, 24));
    private final BoolSetting numbers = add(new BoolSetting("Damage numbers", true));
    private final NumberSetting numberSize = add(new NumberSetting("Number size", 0.5, 2.5, 0.1, 1.0));

    private Map<Integer, Float> lastHealth = new HashMap<>();
    private final List<Floater> floaters = new ArrayList<>();

    private static final class Floater {
        final class_243 pos;
        final String text;
        int age;

        Floater(class_243 pos, String text) {
            this.pos = pos;
            this.text = text;
        }
    }

    public DamageEffects() {
        super("Damage Particles", "Particle burst and floating numbers on damage", Category.PARTICLES, false);
    }

    @Override
    public void onDisable() {
        lastHealth.clear();
        floaters.clear();
    }

    @Override
    public void onTick(class_310 mc) {
        if (mc.field_1687 == null || mc.field_1724 == null) return;

        Iterator<Floater> it = floaters.iterator();
        while (it.hasNext()) {
            Floater f = it.next();
            if (++f.age > 30) it.remove();
        }

        class_2394 effect = ParticleStyles.get(style.get());
        ThreadLocalRandom r = ThreadLocalRandom.current();
        Map<Integer, Float> next = new HashMap<>();

        for (class_1297 e : mc.field_1687.method_18112()) {
            if (!(e instanceof class_1309 le) || e instanceof class_1531) continue;
            float hp = le.method_6032();
            Float prev = lastHealth.get(le.method_5628());
            next.put(le.method_5628(), hp);
            if (prev == null || hp >= prev - 0.01f) continue;
            if (le.method_5858(mc.field_1724) > 64 * 64) continue;

            float dmg = prev - hp;
            class_243 c = le.method_19538().method_1031(0, le.method_17682() * 0.6, 0);
            for (int i = 0; i < amount.getI(); i++) {
                double vx = r.nextDouble() * 2 - 1;
                double vy = r.nextDouble() * 2 - 0.6;
                double vz = r.nextDouble() * 2 - 1;
                mc.field_1687.method_8406(effect, c.field_1352, c.field_1351, c.field_1350, vx * 0.25, vy * 0.25 + 0.05, vz * 0.25);
            }
            if (numbers.get()) {
                floaters.add(new Floater(c.method_1031((r.nextDouble() - 0.5) * 0.5, 0.3, (r.nextDouble() - 0.5) * 0.5),
                        String.format(Locale.ROOT, "-%.1f", dmg)));
            }
        }
        lastHealth = next;
    }

    @Override
    public void onWorldRender(RenderCtx rc) {
        for (Floater f : floaters) {
            float t = (f.age + rc.delta) / 30f;
            if (t >= 1f) continue;
            int alpha = Math.max(8, (int) (255 * (1f - t * t)));
            int color = (alpha << 24) | 0xFF5555;
            class_243 p = f.pos.method_1031(0, t * 1.1, 0);
            double dist = rc.cam.method_1022(p);
            float s = (float) Math.max(0.03, dist * 0.0035) * numberSize.getF();
            RenderUtil.text(rc, class_2561.method_43470(f.text), p, s, color, 0);
        }
    }
}
