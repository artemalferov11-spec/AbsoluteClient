package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.render.RenderUtil;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1293;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5250;

public class Nametags extends Feature {
    private final NumberSetting scale = add(new NumberSetting("Scale", 0.5, 3.0, 0.1, 1.0));
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting mobs = add(new BoolSetting("Mobs", true));
    private final BoolSetting health = add(new BoolSetting("Health", true));
    private final BoolSetting distance = add(new BoolSetting("Distance", true));
    private final BoolSetting armor = add(new BoolSetting("Armor", true));
    private final BoolSetting armorInSight = add(new BoolSetting("Armor only in sight", false));
    private final BoolSetting offhand = add(new BoolSetting("Offhand item", true));
    private final BoolSetting effects = add(new BoolSetting("Potion timers", true));
    private final BoolSetting background = add(new BoolSetting("Background", true));

    public Nametags() {
        super("Nametags", "Nametags through walls with health and distance", Category.VISUALS, false);
    }

    /** Рисуем ли мы свой тег для этого существа (и прячем ли ванильный). */
    public boolean shouldRender(class_1297 e) {
        if (!isEnabled()) return false;
        if (!(e instanceof class_1309) || e instanceof class_1531) return false;
        class_310 mc = class_310.method_1551();
        if (e == mc.field_1724) return !mc.field_1690.method_31044().method_31034();
        return e instanceof class_1657 ? players.get() : mobs.get();
    }

    @Override
    public void onWorldRender(RenderCtx rc) {
        for (class_1297 e : rc.mc.field_1687.method_18112()) {
            if (!shouldRender(e)) continue;
            class_1309 le = (class_1309) e;
            class_243 p = e.method_30950(rc.delta);
            double dist = rc.cam.method_1022(p);
            if (dist > 128) continue;

            class_5250 t = class_2561.method_43473().method_10852(le.method_5476());
            if (health.get()) {
                float hp = le.method_6032();
                float ratio = hp / Math.max(1f, le.method_6063());
                int col = ratio > 0.66f ? 0x55FF55 : ratio > 0.33f ? 0xFFFF55 : 0xFF5555;
                t.method_10852(class_2561.method_43470(String.format(Locale.ROOT, " %.1f", hp)).method_27694(s -> s.method_36139(col)));
            }
            if (distance.get()) {
                t.method_10852(class_2561.method_43470(" " + (int) dist + "m").method_27694(s -> s.method_36139(0xAAAAAA)));
            }

            float s = (float) Math.max(0.025, dist * 0.0028) * scale.getF();
            class_243 pos = p.method_1031(0, e.method_17682() + 0.55 + s * 4, 0);
            RenderUtil.text(rc, t, pos, s, 0xFFFFFFFF, background.get() ? 0x80000000 : 0);

            // Доп. строки над ником (снизу вверх): левая рука, броня, зелья
            List<class_2561> lines = new ArrayList<>();
            boolean isOther = e != rc.mc.field_1724;
            boolean sightOk = !armorInSight.get() || !isOther || rc.mc.field_1724.method_6057(e);

            if (offhand.get()) {
                class_2561 l = offhandLine(le);
                if (l != null) lines.add(l);
            }
            if (armor.get() && sightOk) {
                // от ботинок к шлему, чтобы шлем оказался сверху
                class_1304[] order = {class_1304.field_6166, class_1304.field_6172, class_1304.field_6174, class_1304.field_6169};
                for (class_1304 slot : order) {
                    class_2561 l = armorPieceLine(le.method_6118(slot));
                    if (l != null) lines.add(l);
                }
            }
            if (effects.get()) {
                for (class_1293 inst : le.method_6026()) {
                    lines.add(effectLine(inst));
                }
            }

            float ls = s * 0.8f;
            for (int i = 0; i < lines.size(); i++) {
                RenderUtil.text(rc, lines.get(i), pos.method_1031(0, s * 11 + i * ls * 11, 0), ls, 0xFFFFFFFF,
                        background.get() ? 0x80000000 : 0);
            }
        }
    }

    private static int durColor(int pct) {
        return pct > 66 ? 0x55FF55 : pct > 33 ? 0xFFFF55 : 0xFF5555;
    }

    /** Предмет в левой руке: "L: Название xN". null, если рука пустая. */
    private static class_2561 offhandLine(class_1309 le) {
        class_1799 st = le.method_6079();
        if (st.method_7960()) return null;
        class_5250 t = class_2561.method_43470("L: ").method_27694(style -> style.method_36139(0xAAAAAA));
        t.method_10852(st.method_7964());
        if (st.method_7947() > 1) {
            t.method_10852(class_2561.method_43470(" x" + st.method_7947()).method_27694(style -> style.method_36139(0xFFFF55)));
        }
        return t;
    }

    /** Одна часть брони: "Название 85%". null, если слот пустой. */
    private static class_2561 armorPieceLine(class_1799 st) {
        if (st.method_7960()) return null;
        class_5250 t = class_2561.method_43473();
        t.method_10852(st.method_7964());
        if (st.method_7963()) {
            int pct = 100 - Math.round(100f * st.method_7919() / st.method_7936());
            final int c = durColor(pct);
            t.method_10852(class_2561.method_43470(" " + pct + "%").method_27694(style -> style.method_36139(c)));
        }
        return t;
    }

    /** Эффект зелья: "Название II 3:25" (цвет зависит от остатка времени). */
    private static class_2561 effectLine(class_1293 inst) {
        class_5250 t = class_2561.method_43473();
        t.method_10852(inst.method_5579().comp_349().method_5560());
        int amp = inst.method_5578();
        if (amp > 0) t.method_10852(class_2561.method_43470(" " + (amp + 1)));
        int ticks = inst.method_5584();
        String time;
        int col;
        if (ticks < 0) {
            time = "inf";
            col = 0x55FFFF;
        } else {
            int sec = ticks / 20;
            time = String.format(Locale.ROOT, "%d:%02d", sec / 60, sec % 60);
            col = sec < 10 ? 0xFF5555 : sec < 30 ? 0xFFFF55 : 0x55FF55;
        }
        final int c = col;
        t.method_10852(class_2561.method_43470(" " + time).method_27694(style -> style.method_36139(c)));
        return t;
    }
}
