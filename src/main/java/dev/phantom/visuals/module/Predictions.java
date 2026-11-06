package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.render.RenderUtil;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.Theme;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1764;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4588;
import net.minecraft.class_638;
import net.minecraft.class_746;

public class Predictions extends Feature {
    private final BoolSetting landing = add(new BoolSetting("Landing marker", true));
    private final BoolSetting hits = add(new BoolSetting("Highlight hits", true));
    private final NumberSetting steps = add(new NumberSetting("Max steps", 40, 300, 10, 160));
    private final ModeSetting color = add(new ModeSetting("Color", "Accent", "Accent", "Rainbow", "White"));

    public Predictions() {
        super("Predictions", "Visual trajectory for projectiles", Category.VISUALS, false);
    }

    /** {скорость, гравитация, добавка к pitch} или null, если ничего не метаем. */
    private double[] physics(class_746 p) {
        if (p.method_6115()) {
            class_1799 a = p.method_6030();
            if (a.method_31574(class_1802.field_8102)) {
                float f = (72000 - p.method_6014()) / 20f;
                f = (f * f + f * 2f) / 3f;
                if (f < 0.1f) return null;
                if (f > 1f) f = 1f;
                return new double[]{f * 3.0, 0.05, 0};
            }
            if (a.method_31574(class_1802.field_8547)) return new double[]{2.5, 0.05, 0};
        }
        class_1799[] hands = {p.method_6047(), p.method_6079()};
        for (class_1799 s : hands) {
            if (s.method_31574(class_1802.field_8399) && class_1764.method_7781(s)) return new double[]{3.15, 0.05, 0};
            if (s.method_31574(class_1802.field_8634) || s.method_31574(class_1802.field_8543) || s.method_31574(class_1802.field_8803)) return new double[]{1.5, 0.03, 0};
            if (s.method_31574(class_1802.field_8436) || s.method_31574(class_1802.field_8150)) return new double[]{0.5, 0.05, -20};
            if (s.method_31574(class_1802.field_8287)) return new double[]{0.7, 0.07, -20};
        }
        return null;
    }

    private int col(float t) {
        switch (color.get()) {
            case "Rainbow":
                return 0xFF000000 | class_3532.method_15369((t * 0.8f + (System.currentTimeMillis() % 4000L) / 4000f) % 1f, 0.7f, 1f);
            case "White":
                return 0xFFFFFFFF;
            default:
                return Theme.accent(t * 0.5f);
        }
    }

    @Override
    public void onWorldRender(RenderCtx rc) {
        class_746 p = rc.mc.field_1724;
        class_638 world = rc.mc.field_1687;
        double[] ph = physics(p);
        if (ph == null) return;

        double speed = ph[0], gravity = ph[1], roll = ph[2];
        double rad = Math.PI / 180.0;
        float yaw = p.method_5705(rc.delta), pitch = p.method_5695(rc.delta);
        double dx = -Math.sin(yaw * rad) * Math.cos(pitch * rad);
        double dy = -Math.sin((pitch + roll) * rad);
        double dz = Math.cos(yaw * rad) * Math.cos(pitch * rad);
        class_243 vel = new class_243(dx, dy, dz).method_1029().method_1021(speed);
        class_243 pv = p.method_18798();
        vel = vel.method_1031(pv.field_1352, p.method_24828() ? 0 : pv.field_1351, pv.field_1350);
        class_243 pos = p.method_5836(rc.delta).method_1023(0, 0.1, 0);

        List<class_1297> candidates = new ArrayList<>();
        if (hits.get()) {
            candidates = world.method_8333(p, new class_238(pos, pos).method_1014(110),
                    e -> e instanceof class_1309 && e.method_5805() && !e.method_7325());
        }

        List<class_243> pts = new ArrayList<>();
        pts.add(pos);
        class_243 hitPos = null;
        class_1297 hitEntity = null;

        for (int i = 0; i < steps.getI(); i++) {
            class_243 next = pos.method_1019(vel);
            class_3965 br = world.method_17742(new class_3959(pos, next,
                    class_3959.class_3960.field_17558, class_3959.class_242.field_1348, p));
            boolean blockHit = br.method_17783() != class_239.class_240.field_1333;
            class_243 end = blockHit ? br.method_17784() : next;

            class_1297 he = null;
            class_243 hp = null;
            double best = Double.MAX_VALUE;
            for (class_1297 e : candidates) {
                Optional<class_243> o = e.method_5829().method_1014(0.3).method_992(pos, end);
                if (o.isPresent()) {
                    double d = pos.method_1025(o.get());
                    if (d < best) {
                        best = d;
                        he = e;
                        hp = o.get();
                    }
                }
            }
            if (he != null) {
                pts.add(hp);
                hitEntity = he;
                hitPos = hp;
                break;
            }
            pts.add(end);
            if (blockHit) {
                hitPos = end;
                break;
            }
            pos = next;
            vel = vel.method_1021(0.99).method_1031(0, -gravity, 0);
            if (pos.field_1351 < world.method_31607() - 8) break;
        }

        class_4588 vc = RenderUtil.lines(rc);
        int n = pts.size();
        for (int i = 1; i < n; i++) {
            RenderUtil.line(rc, vc, pts.get(i - 1), pts.get(i), col((float) i / n));
        }
        if (hitEntity != null) {
            RenderUtil.box(rc, vc, hitEntity.method_5829(), 0xFFFF4B4B);
        } else if (landing.get() && hitPos != null) {
            int c = col(1f);
            RenderUtil.box(rc, vc, new class_238(hitPos, hitPos).method_1014(0.12), c);
            RenderUtil.cross(rc, vc, hitPos, 0.3, c);
        }
    }
}
