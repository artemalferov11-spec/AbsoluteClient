package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.ParticleStyles;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_2394;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class WorldParticles extends Feature {
    private final ModeSetting style = add(new ModeSetting("Style", "Snowflake", ParticleStyles.NAMES));
    private final ModeSetting motion = add(new ModeSetting("Motion", "Falling", "Falling", "Rising", "Drifting"));
    private final NumberSetting amount = add(new NumberSetting("Amount", 1, 30, 1, 6));
    private final NumberSetting radius = add(new NumberSetting("Radius", 3, 32, 1, 14));

    public WorldParticles() {
        super("World Particles", "Custom ambient particles around you", Category.PARTICLES, false);
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1687 == null || mc.method_1493()) return;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        class_2394 effect = ParticleStyles.get(style.get());
        double rad = radius.get();

        for (int i = 0; i < amount.getI(); i++) {
            double x = p.method_23317() + (r.nextDouble() * 2 - 1) * rad;
            double z = p.method_23321() + (r.nextDouble() * 2 - 1) * rad;
            double y = p.method_23320() + (r.nextDouble() * 2 - 0.6) * rad * 0.5;
            double vx = (r.nextDouble() - 0.5) * 0.02;
            double vz = (r.nextDouble() - 0.5) * 0.02;
            double vy;
            switch (motion.get()) {
                case "Rising":
                    vy = 0.02 + r.nextDouble() * 0.03;
                    break;
                case "Drifting":
                    vy = (r.nextDouble() - 0.5) * 0.02;
                    break;
                default:
                    vy = -0.03 - r.nextDouble() * 0.03;
            }
            mc.field_1687.method_8406(effect, x, y, z, vx, vy, vz);
        }
    }
}
