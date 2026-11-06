package dev.phantom.visuals.mixin;

import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_310.class)
public interface MinecraftClientAccessor {
    @Accessor("field_1752")
    int phantom$getItemUseCooldown();

    @Accessor("field_1752")
    void phantom$setItemUseCooldown(int value);
}
