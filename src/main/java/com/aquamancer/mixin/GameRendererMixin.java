package com.aquamancer.mixin;

import com.aquamancer.AntiLootrunTracker;
import com.aquamancer.MobProximityList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
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
		if (!AntiLootrunTracker.shouldRenderMobList()) return;

		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.world == null || client.player == null || client.cameraEntity == null) {
			return;
		}

		// get the blockHit under the player's crosshair
		Vec3d eyePos = client.player.getEyePos();
		Vec3d cameraDirection = client.cameraEntity.getRotationVec(tickDelta);
		Vec3d reach = eyePos.add(cameraDirection.multiply(AntiLootrunTracker.config.getMobListReachDistance()));
		RaycastContext raycast = new RaycastContext(
				eyePos,
				reach,
				RaycastContext.ShapeType.OUTLINE,
				RaycastContext.FluidHandling.NONE,
				client.cameraEntity
		);

		BlockHitResult blockHitResult = client.world.raycast(raycast);
		BlockPos blockPos = blockHitResult.getBlockPos();
		Block blockHit = client.world.getBlockState(blockPos).getBlock();

		if (blockHit != Blocks.CHEST) {
			return;
		}

		MobProximityList proximityList = new MobProximityList(AntiLootrunTracker.getMobsNearby(blockPos), Vec3d.of(blockPos));
		Text toDisplay = proximityList.toText();
		if (toDisplay != null) {
			client.inGameHud.setOverlayMessage(proximityList.toText(), false);
		}
	}
}