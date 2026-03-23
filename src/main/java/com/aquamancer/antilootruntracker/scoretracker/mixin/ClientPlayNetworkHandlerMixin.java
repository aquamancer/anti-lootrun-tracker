package com.aquamancer.antilootruntracker.scoretracker.mixin;

import com.aquamancer.antilootruntracker.ShardTracker;
import com.aquamancer.antilootruntracker.scoretracker.ChestBreakListener;
import com.aquamancer.antilootruntracker.scoretracker.ChestOpenListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    // packet sent on chest open, before inventory packet and chest open animation packet
    @Inject(at = @At("HEAD"), method = "onOpenScreen(Lnet/minecraft/network/packet/s2c/play/OpenScreenS2CPacket;)V")
    private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        MinecraftClient.getInstance().execute(() -> {
            ChestOpenListener.onOpenScreen(packet);
        });
    }

    // packet sent on chest open, before onBlockEvent below
    @Inject(at = @At("HEAD"), method = "onInventory(Lnet/minecraft/network/packet/s2c/play/InventoryS2CPacket;)V")
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        MinecraftClient.getInstance().execute(() -> {
            ChestOpenListener.onInventory(packet);
        });
        MinecraftClient client = MinecraftClient.getInstance();
        if (packet != null && client.player != null && packet.getSyncId() != 0) {
//            client.execute(() -> client.player.sendMessage(Text.literal("inventory packet: " + packet.getContents() + " syncid: " + packet.getSyncId())));
//            client.execute(() -> client.player.sendMessage(Text.literal(String.valueOf(System.currentTimeMillis()))));
        }
    }


    // packet sent for chest close/open animation. used to link the inventory packet to the in-world chest
    @Inject(at = @At("HEAD"), method = "onBlockEvent(Lnet/minecraft/network/packet/s2c/play/BlockEventS2CPacket;)V")
    private void onBlockEvent(BlockEventS2CPacket packet, CallbackInfo ci) {
        MinecraftClient.getInstance().execute(() -> {
            ChestOpenListener.onBlockEvent(packet);
        });
        MinecraftClient client = MinecraftClient.getInstance();
        if (packet != null && client.player != null) {
//            client.player.sendMessage(Text.literal(String.valueOf(((ScreenHandlerMixin) client.player.currentScreenHandler).getSyncId())));
            client.execute(() -> {
//                client.player.sendMessage(Text.literal("Block event: { block: " + packet.getBlock() + ", data: " + packet.getData() + ", pos: " + packet.getPos() + ", type: " + packet.getType() + "}"));
//                client.player.sendMessage(Text.literal(String.valueOf(System.currentTimeMillis())));
            });
        }
    }

    @Inject(at = @At("HEAD"), method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V")
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            World world = client.world;
            String shard = ShardTracker.getCurrentShard();
            if (world != null && world.getEntityById(packet.getId()) instanceof ItemEntity item) {
                ChestBreakListener.onEntitySpawn(item, shard);
            }
        });
    }
//
//    @Inject(at = @At("TAIL"), method = "onEntityTrackerUpdate(Lnet/minecraft/network/packet/s2c/play/EntityTrackerUpdateS2CPacket;)V")
//    private void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        client.execute(() -> {
//            Entity updated = MinecraftClient.getInstance().world.getEntityById(packet.id());
//            if (client.player != null && updated instanceof ItemEntity entity && updated.getDisplayName() != null) {
//                client.player.sendMessage(Text.literal("update: " + entity.getId()));
//                client.player.sendMessage(entity.getName());
//                client.player.sendMessage(entity.getStyledDisplayName());
//                client.player.sendMessage(entity.getStack().getName());
//            }
//        });
//    }
}