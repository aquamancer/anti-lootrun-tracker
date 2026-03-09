package com.aquamancer.antilootruntracker.scoretracker.mixin;

import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerMixin {
    @Accessor("syncId")
    int getSyncId();
}
