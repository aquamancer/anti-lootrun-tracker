package com.aquamancer.antilootruntracker.scoretracker.mixin;

import com.aquamancer.antilootruntracker.scoretracker.ScoreTracker;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreenHandler.class)
public abstract class GenericContainerScreenHandlerMixin {
    @Inject(at=@At("HEAD"), method="onClosed(Lnet/minecraft/entity/player/PlayerEntity;)V")
    private void onClosed(PlayerEntity player, CallbackInfo ci) {
        MinecraftClient.getInstance().execute(() -> {
            ScoreTracker.onContainerScreenClosed(((GenericContainerScreenHandler) (Object) this).getInventory());
        });
    }
}
