package com.aquamancer.mixin.client;

import com.aquamancer.AntiLootrunTrackerClient;
import com.aquamancer.MobProximityList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	/**
	 * Enumerates all mobs nearby the Chest being hovered by the player's crosshair.
	 */
	@Inject(at = @At("HEAD"), method = "renderWorld")
	private void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrixStack, CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.world == null || client.player == null || client.cameraEntity == null) {
			return;
		}

		// get the blockHit under the player's crosshair
		Vec3d eyePos = client.player.getEyePos();
		Vec3d cameraDirection = client.cameraEntity.getRotationVec(tickDelta);
		Vec3d reach = eyePos.add(cameraDirection.multiply(20));
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

		// determine if there are entities surrounding the chest
		List<MobEntity> mobEntityList = client.world.getEntitiesByClass(
				MobEntity.class,
				new Box(blockPos).expand(AntiLootrunTrackerClient.MOB_SEARCH_RADIUS),
                LivingEntity::isAlive
		);

		MobProximityList proximityList = new MobProximityList(mobEntityList, Vec3d.of(blockPos));
		Text toDisplay = proximityList.toText();
		if (toDisplay != null) {
			client.inGameHud.setOverlayMessage(proximityList.toText(), false);
		}
	}
}