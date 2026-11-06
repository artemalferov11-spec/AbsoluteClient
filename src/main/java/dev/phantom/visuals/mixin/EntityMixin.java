package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.FreeLook;
import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.class_1297;
import net.minecraft.class_3532;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1297.class)
public abstract class EntityMixin {

    @Inject(method = "method_5851", at = @At("HEAD"), cancellable = true)
    private void phantom$glowInvisible(CallbackInfoReturnable<Boolean> cir) {
        class_1297 self = (class_1297) (Object) this;
        if (!self.method_37908().field_9236) return;
        if (ModuleManager.INVISIBLES.matches(self) || ModuleManager.ESP.glows(self)) {
            cir.setReturnValue(true);
        }
    }

    // FreeLook: мышь крутит камеру, а не игрока
    @Inject(method = "method_5872", at = @At("HEAD"), cancellable = true)
    private void phantom$freeLookMouse(double dx, double dy, CallbackInfo ci) {
        if (FreeLook.active && (Object) this instanceof class_746) {
            FreeLook.yaw += (float) dx * 0.15f;
            FreeLook.pitch = class_3532.method_15363(FreeLook.pitch + (float) dy * 0.15f, -90.0f, 90.0f);
            ci.cancel();
        }
    }
}
