package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.Theme;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_746;

/** Стрелки вокруг прицела, указывающие в сторону игроков (видно и сквозь стены). */
public class Arrows extends Feature {
    private final ModeSetting colorMode = add(new ModeSetting("Color", "Accent", "Accent", "Custom", "Rainbow"));
    private final NumberSetting hue = add(new NumberSetting("Custom hue", 0, 360, 5, 0));
    private final NumberSetting sat = add(new NumberSetting("Custom saturation", 0, 100, 5, 85));
    private final NumberSetting radius = add(new NumberSetting("Radius", 30, 250, 5, 75));
    private final NumberSetting size = add(new NumberSetting("Size", 4, 20, 1, 9));
    private final NumberSetting range = add(new NumberSetting("Range", 8, 256, 8, 96));
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting mobs = add(new BoolSetting("Mobs", false));
    private final BoolSetting distance = add(new BoolSetting("Distance", true));

    public Arrows() {
        super("Arrows", "Arrows around the crosshair pointing at nearby players", Category.VISUALS, false);
    }

    private static float wrap(float a) {
        a = a % 360f;
        if (a >= 180f) a -= 360f;
        if (a < -180f) a += 360f;
        return a;
    }

    private int color(int index) {
        switch (colorMode.get()) {
            case "Custom":
                return 0xFF000000 | class_3532.method_15369(hue.getF() / 360f, sat.getF() / 100f, 1f);
            case "Rainbow":
                float h = ((System.currentTimeMillis() % 4000L) / 4000f + index * 0.07f) % 1f;
                return 0xFF000000 | class_3532.method_15369(h, 0.7f, 1f);
            default:
                return Theme.accent(index * 0.05f);
        }
    }

    private static void dot(class_332 ctx, float x, float y, int c) {
        int ix = Math.round(x), iy = Math.round(y);
        ctx.method_25294(ix, iy, ix + 2, iy + 2, c);
    }

    private static void line(class_332 ctx, float x1, float y1, float x2, float y2, int c) {
        float dx = x2 - x1, dy = y2 - y1;
        int steps = (int) Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)) / 1.5f);
        for (int i = 0; i <= steps; i++) {
            float t = i / (float) steps;
            dot(ctx, x1 + dx * t, y1 + dy * t, c);
        }
    }

    /** Вызывается из HudRenderCallback. */
    public void render(class_332 ctx) {
        if (!isEnabled()) return;
        class_310 mc = class_310.method_1551();
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1687 == null || mc.field_1755 != null) return;

        float cx = mc.method_22683().method_4486() / 2f;
        float cy = mc.method_22683().method_4502() / 2f;
        float yaw = FreeLook.active ? FreeLook.yaw : p.method_36454();
        double r2 = range.get() * range.get();

        List<class_1297> targets = new ArrayList<>();
        for (class_1297 e : mc.field_1687.method_18112()) {
            if (e == p || !(e instanceof class_1309) || e instanceof class_1531) continue;
            if (!(e instanceof class_1657 ? players.get() : mobs.get())) continue;
            if (!e.method_5805() || p.method_5858(e) > r2) continue;
            targets.add(e);
        }
        targets.sort(Comparator.comparingDouble(e -> p.method_5858(e)));

        class_327 font = mc.field_1772;
        int index = 0;
        for (class_1297 e : targets) {
            if (index >= 24) break;
            double dx = e.method_23317() - p.method_23317();
            double dz = e.method_23321() - p.method_23321();
            float rel = wrap((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0) - yaw);
            double a = Math.toRadians(rel);
            float dirX = (float) Math.sin(a), dirY = (float) -Math.cos(a);   // 0° = вверх, + = вправо
            float perpX = -dirY, perpY = dirX;

            double dist = Math.sqrt(p.method_5858(e));
            int alpha = (int) (255 - 130 * Math.min(1.0, dist / range.get()));
            int col = Theme.withAlpha(color(index), alpha);

            float s = size.getF();
            float R = radius.getF();
            float tipX = cx + dirX * (R + s), tipY = cy + dirY * (R + s);
            float baseX = cx + dirX * (R - s * 0.5f), baseY = cy + dirY * (R - s * 0.5f);
            float w1x = baseX + perpX * s * 0.8f, w1y = baseY + perpY * s * 0.8f;
            float w2x = baseX - perpX * s * 0.8f, w2y = baseY - perpY * s * 0.8f;

            int n = Math.max(3, (int) (s * 1.2f));
            for (int i = 0; i <= n; i++) {
                float t = i / (float) n;
                line(ctx, tipX, tipY, w1x + (w2x - w1x) * t, w1y + (w2y - w1y) * t, col);
            }

            if (distance.get()) {
                String txt = (int) dist + "m";
                float tx = cx + dirX * (R + s + 12) - font.method_1727(txt) / 2f;
                float ty = cy + dirY * (R + s + 12) - 4;
                ctx.method_51433(font, txt, Math.round(tx), Math.round(ty), Theme.withAlpha(0xFFFFFF, alpha), true);
            }
            index++;
        }
    }
}
