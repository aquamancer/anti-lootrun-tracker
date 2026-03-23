package com.aquamancer.antilootruntracker.scoretracker.mixin;

import com.aquamancer.antilootruntracker.scoretracker.ChestBreakListener;
import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientEntityManager.class)
public class ClientEntityManagerMixin {
//    @Inject(at=@At("HEAD"), method="addEntity(Lnet/minecraft/world/entity/EntityLike;)V")
//    private void addEntity(EntityLike e, CallbackInfo ci) {
//        if (e instanceof ItemEntity entity) {
//            ChestBreakListener.onItemDrop(entity);
//        }
//    }
}
