package dev.phantom.visuals.mixin;

import dev.phantom.visuals.gui.PhantomTitleScreen;
import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_442;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_310.class)
public abstract class MinecraftClientMixin {
    // method_1507 = setScreen. Подменяем ванильное главное меню на своё
    @ModifyVariable(method = "method_1507", at = @At("HEAD"), argsOnly = true, remap = false)
    private class_437 phantom$customTitle(class_437 screen) {
        if (screen != null && screen.getClass() == class_442.class && ModuleManager.MENU.isEnabled()) {
            return new PhantomTitleScreen();
        }
        return screen;
    }
}
