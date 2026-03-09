package com.aquamancer.antilootruntracker.scoretracker.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    // packet sent on chest open, before inventory packet and chest open animation packet
    @Inject(at=@At("HEAD"), method="onOpenScreen(Lnet/minecraft/network/packet/s2c/play/OpenScreenS2CPacket;)V")
    private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (packet != null && client.player != null) {
            client.execute(() -> {
                client.player.sendMessage(Text.literal("onOpenScreen: {name: " + ((TranslatableTextContent)packet.getName().getContent()).getKey() + ", screenhandlertype: " + packet.getScreenHandlerType() + ", syncid: " + packet.getSyncId() + "}"));
                client.player.sendMessage(Text.literal(String.valueOf(System.currentTimeMillis())));
            });
        }
    }

    // packet sent on chest open, before onBlockEvent below
    @Inject(at=@At("HEAD"), method="onInventory(Lnet/minecraft/network/packet/s2c/play/InventoryS2CPacket;)V")
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (packet != null && client.player != null) {
            client.execute(() -> client.player.sendMessage(Text.literal("inventory packet: " + packet.getContents().get(0) + " syncid: " + packet.getSyncId())));
            client.execute(() -> client.player.sendMessage(Text.literal(String.valueOf(System.currentTimeMillis()))));
        }
    }


    // packet sent for chest close/open animation. used to link the inventory packet to the in-world chest
    @Inject(at=@At("HEAD"), method="onBlockEvent(Lnet/minecraft/network/packet/s2c/play/BlockEventS2CPacket;)V")
    private void onBlockEvent(BlockEventS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (packet != null && client.player != null) {
            client.execute(() -> {
                client.player.sendMessage(Text.literal("Block event: { block: " + packet.getBlock() + ", data: " + packet.getData() + ", pos: " + packet.getPos() + ", type: " + packet.getType() + "}"));
                client.player.sendMessage(Text.literal(String.valueOf(System.currentTimeMillis())));
            });
        }
    }
}
