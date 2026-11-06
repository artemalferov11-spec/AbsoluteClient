package dev.phantom.visuals.module;

/** Не замедляет при еде/питье/щите. Логика — в миксинах (ClientPlayerMixin + LivingEntityMixin). */
public class NoSlow extends Feature {
    /** true только пока выполняется ClientPlayerEntity#tickMovement. */
    public static boolean inMovementTick = false;

    public NoSlow() {
        super("NoSlow", "No slowdown while eating / drinking / using items", Category.MOVEMENT, false);
    }
}
