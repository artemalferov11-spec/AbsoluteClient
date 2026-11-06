package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import net.minecraft.class_916;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_916.class)
public abstract class ItemEntityRendererMixin {

    // 1) убираем покачивание вверх-вниз
    @Redirect(
            method = "method_3996(Lnet/minecraft/class_10039;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3532;method_15374(F)F"))
    private float phantom$noBob(float value) {
        return ModuleManager.ITEM_PHYSIC.isEnabled() ? 0f : class_3532.method_15374(value);
    }

    // 2) убираем вращение: фиксированный угол у каждого предмета
    @Redirect(
            method = "method_3996(Lnet/minecraft/class_10039;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1542;method_27314(FF)F"))
    private float phantom$noSpin(float age, float offset) {
        return ModuleManager.ITEM_PHYSIC.isEnabled() ? offset : age / 20.0f + offset;
    }

    // 3) кладём предмет плашмя на землю
    @Redirect(
            method = "method_3996(Lnet/minecraft/class_10039;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4587;method_22907(Lorg/joml/Quaternionf;)V"))
    private void phantom$lieFlat(class_4587 matrices, Quaternionf rotation) {
        if (ModuleManager.ITEM_PHYSIC.isEnabled()) {
            matrices.method_22904(0.0, -ModuleManager.ITEM_PHYSIC.drop.get(), 0.0);
            matrices.method_22907(rotation);
            matrices.method_22907(class_7833.field_40714.rotationDegrees(90.0f));
        } else {
            matrices.method_22907(rotation);
        }
    }
}
