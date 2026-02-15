package com.aquamancer.antilootruntracker.mixin;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerListHud.class)
public interface TabHudAccessor {
    // used to get shard info by parsing Tab header string
    @Accessor("header")
    Text getHeader();
}
