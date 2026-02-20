package com.aquamancer.antilootruntracker.mixin;

import com.aquamancer.antilootruntracker.moblist.MobListManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    /**
     * Prevents mob list from completely blocking out other action bar messages
     */
    @Inject(at = @At("HEAD"), method = "onOverlayMessage")
    public void onOverlayMessage(OverlayMessageS2CPacket packet, CallbackInfo ci) {
        MobListManager.disableMobListTemporarily();
    }
}
