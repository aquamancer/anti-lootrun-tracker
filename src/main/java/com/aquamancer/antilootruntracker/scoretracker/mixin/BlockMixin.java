package com.aquamancer.antilootruntracker.scoretracker.mixin;

import com.aquamancer.antilootruntracker.scoretracker.ChestBreakListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(at=@At("HEAD"), method="Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;")
    private void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
        ChestBreakListener.onBlockMined(world, pos, state);
    }

//    @Inject(at=@At("HEAD"), method="Lnet/minecraft/block/Block;onBroken(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V")
//    private void onBroken(WorldAccess world, BlockPos pos, BlockState state, CallbackInfo ci) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.player != null) {
//            client.player.sendMessage(Text.literal("onBroken: " + pos.toString()));
//        }
//    }
//
// works, but not needed
//    @Inject(at=@At("HEAD"), method="Lnet/minecraft/block/Block;replace(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;I)V")
//    private static void onReplace(BlockState state, BlockState newState, WorldAccess world, BlockPos pos, int flags, CallbackInfo ci) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.player != null) {
//            client.player.sendMessage(Text.literal("onReplaceI: " + pos.toString()));
//        }
//    }
//
//    @Inject(at=@At("HEAD"), method="Lnet/minecraft/block/Block;replace(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V")
//    private static void onReplace(BlockState state, BlockState newState, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.player != null) {
//            client.player.sendMessage(Text.literal("onReplaceII: " + pos.toString()));
//        }
//    }
//
//    @Inject(at=@At("HEAD"), method="Lnet/minecraft/block/Block;onDestroyedByExplosion(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/explosion/Explosion;)V")
//    private void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion, CallbackInfo ci) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.player != null) {
//            client.player.sendMessage(Text.literal("exploded: " + pos.toString()));
//        }
//    }
}
