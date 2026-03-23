package com.aquamancer.antilootruntracker.scoretracker.mixin;

import com.aquamancer.antilootruntracker.scoretracker.ChestBreakListener;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class WorldMixin {
    @Inject(at=@At("HEAD"), method="addEntity(Lnet/minecraft/entity/Entity;)V")
    private void addEntity(Entity e, CallbackInfo ci) {
//        if (e instanceof ItemEntity entity) {
//            ChestBreakListener.onItemDrop(entity);
//        }
    }
}
