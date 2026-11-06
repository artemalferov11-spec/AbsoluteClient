package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class KillAura extends Feature {
    private final NumberSetting range = add(new NumberSetting("Range", 2.0, 6.0, 0.1, 3.5));
    private final NumberSetting fov = add(new NumberSetting("FOV (degrees)", 10, 360, 10, 360));
    private final ModeSetting hitMode = add(new ModeSetting("Hit mode", "Tap-tap", "Tap-tap", "Crits only"));
    private final ModeSetting priority = add(new ModeSetting("Priority", "Distance", "Distance", "Health", "Angle"));
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting mobs = add(new BoolSetting("Mobs", false));
    private final BoolSetting walls = add(new BoolSetting("Through walls", false));
    private final BoolSetting invisibles = add(new BoolSetting("Invisibles", true));

    public KillAura() {
        super("KillAura", "Automatically attacks the nearest target in range / FOV", Category.COMBAT, false);
    }

    /** true, если включён режим критов — AutoSprint в этом случае не должен снова включать спринт. */
    public boolean critMode() {
        return isEnabled() && hitMode.is("Crits only");
    }

    private static float wrap(float a) {
        a = a % 360f;
        if (a >= 180f) a -= 360f;
        if (a < -180f) a += 360f;
        return a;
    }

    /** Ванильные условия крита (PlayerEntity#attack): падение, не на земле, не спринт, не вода/лестница/транспорт. */
    private static boolean canCrit(class_746 p) {
        return !p.method_24828()
                && p.field_6017 > 0.0f
                && p.method_18798().field_1351 < 0.0      // реально падаем, а не взлетаем
                && !p.method_5624()                       // спринт отключает крит
                && !p.method_6101()                       // лестница/лоза
                && !p.method_5799()                       // вода
                && !p.method_5765();                      // транспорт
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1687 == null || mc.field_1761 == null || mc.field_1755 != null) return;
        if (p.method_7261(0.5f) < 1.0f) return; // бьём только на полной перезарядке

        if (hitMode.is("Crits only") && !canCrit(p)) return;

        double r2 = range.get() * range.get();
        float halfFov = fov.getF() / 2f;
        class_243 pp = p.method_19538();
        class_1309 best = null;
        double bestScore = Double.MAX_VALUE;
        for (class_1297 e : mc.field_1687.method_18112()) {
            if (e == p || !(e instanceof class_1309 le) || e instanceof class_1531) continue;
            if (le.method_6032() <= 0f) continue;
            if (!(e instanceof class_1657 ? players.get() : mobs.get())) continue;
            if (!invisibles.get() && le.method_5767()) continue;
            double d = p.method_5858(le);
            if (d > r2) continue;
            class_243 tp = le.method_19538();
            float want = (float) (Math.toDegrees(Math.atan2(tp.field_1350 - pp.field_1350, tp.field_1352 - pp.field_1352)) - 90.0);
            float diff = Math.abs(wrap(want - p.method_36454()));
            if (fov.get() < 360 && diff > halfFov) continue;
            if (!walls.get() && !p.method_6057(le)) continue;
            double score = priority.is("Health") ? le.method_6032() : priority.is("Angle") ? diff : d;
            if (score >= bestScore) continue;
            best = le;
            bestScore = score;
        }
        if (best == null) return;

        mc.field_1761.method_2918(p, best);
        p.method_6104(class_1268.field_5808);
    }
}
