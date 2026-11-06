package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.class_1309;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_922.class)
public abstract class LivingEntityRendererMixin {

    // Прячем ванильный тег, когда рисуем свой (иначе будет дубль)
    @Inject(method = "method_4055(Lnet/minecraft/class_1309;D)Z", at = @At("HEAD"), cancellable = true)
    private void phantom$hideVanillaLabel(class_1309 entity, double distance, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.NAMETAGS.shouldRender(entity)) {
            cir.setReturnValue(false);
        }
    }
}
