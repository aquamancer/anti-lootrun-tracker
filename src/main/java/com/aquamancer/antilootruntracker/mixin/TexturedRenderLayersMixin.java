package com.aquamancer.antilootruntracker.mixin;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import com.aquamancer.antilootruntracker.ColorManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {
    /**
     * Replaces Chest textures with recolored textures based on how many mobs are near the individual Chests.
     */
    @Inject(at = @At("HEAD"), cancellable = true, method = "getChestTextureId(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier;")
    private static void getChestTexture(BlockEntity chest, ChestType type, boolean christmas, CallbackInfoReturnable<SpriteIdentifier> cir) {
        if (!AntiLootrunTracker.shouldRecolorFreeChest()) {
            return;
        }
        if (!(chest instanceof ChestBlockEntity)) {
            return;
        }

        SpriteIdentifier sprite = ColorManager.getChestSpriteBasedOnMobs(chest, type);
        if (sprite != null) {
            cir.setReturnValue(sprite);
        }
    }
}
