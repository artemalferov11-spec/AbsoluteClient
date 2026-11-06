package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.FreeCam;
import dev.phantom.visuals.module.NoSlow;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_746.class)
public abstract class ClientPlayerMixin {
    // method_6007 = tickMovement. Пока он выполняется, isUsingItem() у игрока врёт "false" (см. LivingEntityMixin)
    @Inject(method = "method_6007", at = @At("HEAD"), remap = false, cancellable = true)
    private void phantom$tickStart(CallbackInfo ci) {
        // FreeCam: персонаж стоит на месте, пока летает камера
        if (FreeCam.active) {
            ci.cancel();
            return;
        }
        NoSlow.inMovementTick = true;
    }

    @Inject(method = "method_6007", at = @At("RETURN"), remap = false)
    private void phantom$tickEnd(CallbackInfo ci) {
        NoSlow.inMovementTick = false;
    }
}
