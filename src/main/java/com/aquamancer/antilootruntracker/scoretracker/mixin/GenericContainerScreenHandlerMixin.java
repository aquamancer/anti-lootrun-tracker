package com.aquamancer.antilootruntracker.scoretracker.mixin;

import com.aquamancer.antilootruntracker.scoretracker.LootingTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreenHandler.class)
public class GenericContainerScreenHandlerMixin {
    @Inject(at=@At("HEAD"), method="onClosed(Lnet/minecraft/entity/player/PlayerEntity;)V")
    private void onClosed(PlayerEntity player, CallbackInfo ci) {
        MinecraftClient.getInstance().execute(() -> {
            LootingTracker.onContainerScreenClosed(((GenericContainerScreenHandler) (Object) this).getInventory());
        });
    }
}
