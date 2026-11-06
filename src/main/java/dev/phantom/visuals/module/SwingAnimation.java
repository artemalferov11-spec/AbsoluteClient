package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import net.minecraft.class_4587;
import org.joml.Quaternionf;

/**
 * Анимации руки/меча.
 * Mode  - кривая прогресса взмаха (LivingEntityMixin).
 * Pose  - постоянное положение меча в руке (HeldItemRendererMixin).
 * Motion - дополнительное движение во время взмаха (HeldItemRendererMixin).
 * Rot/Pos - ручная подстройка поверх выбранной позы.
 */
public class SwingAnimation extends Feature {
    public final ModeSetting mode = add(new ModeSetting("Mode", "Smooth", "Vanilla", "Smooth", "Ease-in", "Ease-out", "Snap", "Wave"));
    public final NumberSetting speed = add(new NumberSetting("Speed", 0.5, 3.0, 0.1, 1.0));
    public final ModeSetting pose = add(new ModeSetting("Pose", "Sideways", "None", "Sideways", "Sideways flip", "Flat", "Tilt", "Custom"));
    public final ModeSetting motion = add(new ModeSetting("Motion", "Slash", "None", "Slash", "Chop", "Poke", "Stab", "Spin", "Wiggle"));
    public final NumberSetting rotX = add(new NumberSetting("Rot X", -180, 180, 5, 0));
    public final NumberSetting rotY = add(new NumberSetting("Rot Y", -180, 180, 5, 0));
    public final NumberSetting rotZ = add(new NumberSetting("Rot Z", -180, 180, 5, 0));
    public final NumberSetting posX = add(new NumberSetting("Pos X", -1, 1, 0.05, 0));
    public final NumberSetting posY = add(new NumberSetting("Pos Y", -1, 1, 0.05, 0));
    public final NumberSetting posZ = add(new NumberSetting("Pos Z", -1, 1, 0.05, 0));

    public SwingAnimation() {
        super("SwingAnimation", "Changes sword swing animation and how the sword is held", Category.VISUALS, false);
    }

    /** Преобразует прогресс взмаха 0..1. */
    public float apply(float p) {
        if (p <= 0f || p >= 1f) return p;
        switch (mode.get()) {
            case "Smooth":
                return p * p * (3f - 2f * p);
            case "Ease-in":
                return p * p;
            case "Ease-out":
                return (float) Math.sqrt(p);
            case "Snap":
                return Math.min(1f, p * 1.8f);
            case "Wave":
                return p + 0.12f * (float) Math.sin(p * Math.PI * 2.0);
            default:
                return p;
        }
    }

    /** Вызывается в начале отрисовки предмета в первом лице (правая рука). */
    public void transform(class_4587 m, float sw) {
        float rx = rotX.getF(), ry = rotY.getF(), rz = rotZ.getF();
        float px = posX.getF(), py = posY.getF(), pz = posZ.getF();

        switch (pose.get()) {
            case "Sideways" -> rz += -75f;
            case "Sideways flip" -> rz += 75f;
            case "Flat" -> rx += 75f;
            case "Tilt" -> {
                rx += -25f;
                ry += 25f;
                rz += -20f;
            }
            default -> {
            }
        }

        float s = (float) Math.sin(sw * Math.PI);
        switch (motion.get()) {
            case "Slash" -> {
                rz += -70f * s;
                ry += 30f * s;
            }
            case "Chop" -> rx += -80f * s;
            case "Poke" -> pz += -0.5f * s;
            case "Stab" -> {
                pz += -0.6f * s;
                rx += -20f * s;
            }
            case "Spin" -> rx += 360f * sw;
            case "Wiggle" -> rz += 25f * (float) Math.sin(sw * Math.PI * 4.0);
            default -> {
            }
        }

        if (rx == 0f && ry == 0f && rz == 0f && px == 0f && py == 0f && pz == 0f) return;

        // вращаем вокруг того места, где обычно держится предмет в правой руке
        m.method_22904(px, py, pz);
        m.method_22904(0.56, -0.52, -0.72);
        m.method_22907(new Quaternionf().rotateXYZ(
                (float) Math.toRadians(rx), (float) Math.toRadians(ry), (float) Math.toRadians(rz)));
        m.method_22904(-0.56, 0.52, 0.72);
    }
}
