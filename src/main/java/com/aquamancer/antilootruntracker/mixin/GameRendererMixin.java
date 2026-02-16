package com.aquamancer.antilootruntracker.mixin;

import com.aquamancer.antilootruntracker.moblist.MobDistanceList;
import com.aquamancer.antilootruntracker.moblist.MobListManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	/**
	 * Enumerates all mobs nearby the Chest being hovered by the player's crosshair to the action bar.
	 */
	@Inject(at = @At("HEAD"), method = "renderWorld")
	private void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrixStack, CallbackInfo ci) {
		if (!MobListManager.shouldRenderMobList()) return;

		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.inGameHud == null) {
			return;
		}

		MobDistanceList mobList = MobListManager.getActionBarMobList();
		if (mobList == null) return;
		Text message = mobList.toText();
		if (message != null) {
			client.inGameHud.setOverlayMessage(message, false);
		}
	}
}