package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.ModuleManager;
import dev.phantom.visuals.module.NoSlow;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1309.class)
public abstract class LivingEntityMixin {

    // NoSlow: method_6115 = isUsingItem
    @Inject(method = "method_6115", at = @At("HEAD"), cancellable = true, remap = false)
    private void phantom$noSlow(CallbackInfoReturnable<Boolean> cir) {
        if (NoSlow.inMovementTick && ModuleManager.NOSLOW.isEnabled() && (Object) this instanceof class_746) {
            cir.setReturnValue(false);
        }
    }

    // SwingAnimation: method_6055 = getHandSwingProgress(float)
    @Inject(method = "method_6055", at = @At("RETURN"), cancellable = true, remap = false)
    private void phantom$swingCurve(float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (ModuleManager.SWING.isEnabled() && (Object) this == class_310.method_1551().field_1724) {
            cir.setReturnValue(ModuleManager.SWING.apply(cir.getReturnValueF()));
        }
    }

    // SwingAnimation: method_6028 = getHandSwingDuration (скорость взмаха)
    @Inject(method = "method_6028", at = @At("RETURN"), cancellable = true, remap = false)
    private void phantom$swingSpeed(CallbackInfoReturnable<Integer> cir) {
        if (ModuleManager.SWING.isEnabled() && (Object) this == class_310.method_1551().field_1724) {
            int d = Math.max(1, Math.round(cir.getReturnValueI() / ModuleManager.SWING.speed.getF()));
            cir.setReturnValue(d);
        }
    }
}
