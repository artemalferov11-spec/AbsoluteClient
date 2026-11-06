package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.class_243;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_638.class)
public abstract class ClientWorldMixin {
    // method_23777 = ClientWorld#getSkyColor(Vec3d, float)
    @Inject(method = "method_23777", at = @At("RETURN"), cancellable = true, remap = false)
    private void phantom$skyColor(CallbackInfoReturnable<class_243> cir) {
        if (ModuleManager.SKY.isEnabled()) {
            cir.setReturnValue(ModuleManager.SKY.color());
        }
    }
}
