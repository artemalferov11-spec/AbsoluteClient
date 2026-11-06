package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.FreeCam;
import dev.phantom.visuals.module.FreeLook;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(class_4184.class)
public abstract class CameraMixin {

    // Во время FreeLook камера смотрит в свою сторону, а не туда, куда повёрнут игрок
    @ModifyArgs(
            method = "method_19321(Lnet/minecraft/class_1922;Lnet/minecraft/class_1297;ZZF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4184;method_19325(FF)V"))
    private void phantom$freeLook(Args args) {
        if (FreeLook.active) {
            args.set(0, FreeLook.yaw);
            args.set(1, FreeLook.pitch);
        }
    }

    // FreeCam: method_19327 = Camera#setPos(double, double, double) — подменяем позицию камеры
    @ModifyArgs(
            method = "method_19321(Lnet/minecraft/class_1922;Lnet/minecraft/class_1297;ZZF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4184;method_19327(DDD)V"))
    private void phantom$freeCamPos(Args args) {
        if (FreeCam.active) {
            double[] p = FreeCam.stepPos();
            args.set(0, p[0]);
            args.set(1, p[1]);
            args.set(2, p[2]);
        }
    }
}
