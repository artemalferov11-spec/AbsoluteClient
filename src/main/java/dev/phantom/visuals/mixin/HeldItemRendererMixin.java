package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_759.class)
public abstract class HeldItemRendererMixin {
    // method_3228 = renderFirstPersonItem
    @Inject(method = "method_3228", at = @At("HEAD"), remap = false)
    private void phantom$swordPose(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress,
                                   class_1799 item, float equipProgress, class_4587 matrices, class_4597 consumers,
                                   int light, CallbackInfo ci) {
        if (hand == class_1268.field_5808 && !item.method_7960() && ModuleManager.SWING.isEnabled()) {
            ModuleManager.SWING.transform(matrices, swingProgress);
        }
    }
}
