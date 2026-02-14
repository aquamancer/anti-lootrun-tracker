package com.aquamancer.mixin.client;

import com.aquamancer.AntiLootrunTrackerClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    /**
     * Temporarily disables the mob list action bar rendering so the player can see the server's
     * "chest is locked" message
     */
    @Inject(at = @At("HEAD"), method = "onOverlayMessage")
    public void onOverlayMessage(OverlayMessageS2CPacket packet, CallbackInfo ci) {
        AntiLootrunTrackerClient.disableMobList(20);  // disable for 1 second
    }
}
