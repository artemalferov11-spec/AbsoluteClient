package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_746;

/** Поставил обсидиан -> мод ставит кристалл сверху и взрывает его. */
public class AutoCrystal extends Feature {
    private final NumberSetting placeRange = add(new NumberSetting("Place range", 2.0, 5.0, 0.1, 4.5));
    private final NumberSetting breakRange = add(new NumberSetting("Break range", 2.0, 5.0, 0.1, 4.5));
    private final NumberSetting placeDelay = add(new NumberSetting("Place delay (ticks)", 0, 10, 1, 1));
    private final NumberSetting breakDelay = add(new NumberSetting("Break delay (ticks)", 0, 10, 1, 1));
    private final NumberSetting minHealth = add(new NumberSetting("Min own health", 0, 20, 1, 8));
    private final BoolSetting autoSwitch = add(new BoolSetting("Auto switch to crystal", true));
    private final BoolSetting bedrock = add(new BoolSetting("Also on bedrock", false));

    private static final int SCAN = 5;

    private record P(int x, int y, int z) {}

    private Set<P> lastObsidian = new HashSet<>();
    private int lastX, lastY, lastZ;
    private boolean primed;
    private final Map<P, Integer> pending = new HashMap<>();   // обсидиан, на который надо поставить кристалл
    private final Map<P, Integer> placed = new HashMap<>();    // позиции кристаллов, поставленных нами
    private int placeTimer, breakTimer;
    private int prevSlot = -1;

    public AutoCrystal() {
        super("AutoCrystal", "Place obsidian: auto places an end crystal on it and detonates it", Category.COMBAT, false);
    }

    @Override
    public void onDisable() {
        reset();
    }

    private void reset() {
        lastObsidian = new HashSet<>();
        pending.clear();
        placed.clear();
        primed = false;
        prevSlot = -1;
    }

    private boolean isBase(class_2680 st) {
        return st.method_26204() == class_2246.field_10540 || (bedrock.get() && st.method_26204() == class_2246.field_9987);
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1687 == null || mc.field_1761 == null) {
            reset();
            return;
        }
        if (mc.field_1755 != null) return;

        class_243 pp = p.method_19538();
        int cx = (int) Math.floor(pp.field_1352), cy = (int) Math.floor(pp.field_1351), cz = (int) Math.floor(pp.field_1350);

        // 1) ищем НОВЫЙ обсидиан (появился в зоне, которую сканировали и в прошлый тик)
        Set<P> cur = new HashSet<>();
        for (int x = cx - SCAN; x <= cx + SCAN; x++)
            for (int y = cy - SCAN; y <= cy + SCAN; y++)
                for (int z = cz - SCAN; z <= cz + SCAN; z++) {
                    if (isBase(mc.field_1687.method_8320(new class_2338(x, y, z)))) cur.add(new P(x, y, z));
                }
        if (primed) {
            for (P q : cur) {
                boolean inOldCube = Math.abs(q.x - lastX) <= SCAN && Math.abs(q.y - lastY) <= SCAN && Math.abs(q.z - lastZ) <= SCAN;
                if (inOldCube && !lastObsidian.contains(q)) pending.put(q, 60);
            }
        }
        lastObsidian = cur;
        lastX = cx;
        lastY = cy;
        lastZ = cz;
        primed = true;

        // таймеры жизни
        decay(pending);
        decay(placed);
        if (placeTimer > 0) placeTimer--;
        if (breakTimer > 0) breakTimer--;

        // 2) ставим кристалл
        if (placeTimer == 0 && !pending.isEmpty()) {
            P target = null;
            double bd = Double.MAX_VALUE;
            double pr2 = placeRange.get() * placeRange.get();
            for (P q : new ArrayList<>(pending.keySet())) {
                if (!isBase(mc.field_1687.method_8320(new class_2338(q.x, q.y, q.z)))) {
                    pending.remove(q);
                    continue;
                }
                if (!mc.field_1687.method_8320(new class_2338(q.x, q.y + 1, q.z)).method_26215()
                        || !mc.field_1687.method_8320(new class_2338(q.x, q.y + 2, q.z)).method_26215()) continue;
                double dx = q.x + 0.5 - pp.field_1352, dy = q.y + 1.0 - (pp.field_1351 + 1.6), dz = q.z + 0.5 - pp.field_1350;
                double d = dx * dx + dy * dy + dz * dz;
                if (d > pr2 || d >= bd) continue;
                target = q;
                bd = d;
            }
            if (target != null && placeCrystal(mc, p, target)) {
                pending.remove(target);
                placed.put(new P(target.x, target.y + 1, target.z), 60);
                placeTimer = placeDelay.getI();
            }
        }

        // 3) взрываем свои кристаллы
        if (breakTimer == 0 && !placed.isEmpty() && p.method_6032() >= minHealth.getI()) {
            double br2 = breakRange.get() * breakRange.get();
            for (class_1297 e : mc.field_1687.method_18112()) {
                if (!(e instanceof class_1511)) continue;
                class_243 c = e.method_19538();
                P key = new P((int) Math.floor(c.field_1352), (int) Math.floor(c.field_1351), (int) Math.floor(c.field_1350));
                if (!placed.containsKey(key)) continue;
                if (p.method_5858(e) > br2) continue;
                mc.field_1761.method_2918(p, e);
                p.method_6104(class_1268.field_5808);
                placed.remove(key);
                breakTimer = breakDelay.getI();
                break;
            }
        }

        // 4) вернули слот, когда делать больше нечего
        if (prevSlot >= 0 && pending.isEmpty() && placed.isEmpty()) {
            p.method_31548().field_7545 = prevSlot;
            prevSlot = -1;
        }
    }

    private static <K> void decay(Map<K, Integer> map) {
        Iterator<Map.Entry<K, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, Integer> en = it.next();
            if (en.getValue() <= 1) it.remove();
            else en.setValue(en.getValue() - 1);
        }
    }

    private boolean placeCrystal(class_310 mc, class_746 p, P base) {
        class_1268 hand;
        if (p.method_6079().method_31574(class_1802.field_8301)) {
            hand = class_1268.field_5810; // левая рука
        } else if (p.method_6047().method_31574(class_1802.field_8301)) {
            hand = class_1268.field_5808;
        } else {
            if (!autoSwitch.get()) return false;
            int slot = -1;
            for (int i = 0; i < 9; i++) {
                if (p.method_31548().method_5438(i).method_31574(class_1802.field_8301)) {
                    slot = i;
                    break;
                }
            }
            if (slot < 0) return false;
            if (prevSlot < 0) prevSlot = p.method_31548().field_7545;
            p.method_31548().field_7545 = slot;
            hand = class_1268.field_5808;
        }
        class_3965 hit = new class_3965(new class_243(base.x + 0.5, base.y + 1.0, base.z + 0.5),
                class_2350.field_11036, new class_2338(base.x, base.y, base.z), false);
        mc.field_1761.method_2896(p, hand, hit);
        p.method_6104(hand);
        return true;
    }
}
