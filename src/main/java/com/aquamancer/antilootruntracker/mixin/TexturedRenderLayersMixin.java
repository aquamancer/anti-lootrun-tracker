package com.aquamancer.antilootruntracker.mixin;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import com.aquamancer.antilootruntracker.RecolorManager;
import com.aquamancer.antilootruntracker.MobScanner;
import com.aquamancer.antilootruntracker.ShardInfo;
import com.aquamancer.antilootruntracker.config.ModConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
        if (!recolorFreeChests() && !recolorAllChests()) {
            return;
        }
        // if !hasWorld() then the chest is being rendered in an inventory
        if (!(chest instanceof ChestBlockEntity) || !chest.hasWorld()) {
            return;
        }

        SpriteIdentifier sprite = null;
        long mobsNearby = MobScanner.getMobsNearby(chest.getPos()).count();
        if (mobsNearby <= 0 && recolorFreeChests()) {
            sprite = RecolorManager.getChestSprite(AntiLootrunTracker.config.getFreeChestRecolor(), type);
        } else if (recolorAllChests()) {
            sprite = RecolorManager.getChestSprite(AntiLootrunTracker.config.getAllChestRecolor(), type);
        }

        if (sprite != null) {
            cir.setReturnValue(sprite);
        }
    }

    @Unique
    private static boolean recolorFreeChests() {
                ModConfig config = AntiLootrunTracker.config;
                return config.isModEnabled() && ShardInfo.inValidShard() && config.recolorFreeChests();
            }

    @Unique
    private static boolean recolorAllChests() {
        ModConfig config = AntiLootrunTracker.config;
        return config.isModEnabled()
                    && config.recolorAllChests()
                    && (config.shouldAllChestRecolorIgnoreShard() || ShardInfo.inValidShard());
    }
}
