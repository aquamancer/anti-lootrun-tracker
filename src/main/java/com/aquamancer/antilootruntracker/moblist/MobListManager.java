package com.aquamancer.antilootruntracker.moblist;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

/**
 * Holds the state of the mob list to be rendered, and handles update logic.
 * Related mixin: GameRendererMixin.java
 */
public class MobListManager {
    private static MobDistanceList activeList;
    private static int ticksUntilUpdate = 0;
    private static int disabledDuration = 0;

    public static void onTick() {
        if (ticksUntilUpdate > 0) {
            ticksUntilUpdate--;
        }
        if (disabledDuration > 0) {
            disabledDuration--;
        }

        if (ticksUntilUpdate <= 0) {
            update();
            ticksUntilUpdate = AntiLootrunTracker.config.getMobListUpdateInterval();
        }
    }

    private static void update() {
        if (!shouldRenderMobList()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || client.player == null || client.cameraEntity == null) {
            return;
        }

        // get the blockHit under the player's crosshair
        Vec3d eyePos = client.player.getEyePos();
        Vec3d cameraDirection = client.cameraEntity.getRotationVec(client.getTickDelta());
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

        activeList = new MobDistanceList(AntiLootrunTracker.getMobsNearby(blockPos), Vec3d.of(blockPos));
    }

    public static void disableMobListTemporarily() {
        int duration = AntiLootrunTracker.config.getMobListDisableDuration();
        if (duration > disabledDuration) {
            disabledDuration = duration;
        }
    }

    @Nullable
    public static MobDistanceList getActionBarMobList() {
        return activeList;
    }

    public static boolean shouldRenderMobList() {
        return AntiLootrunTracker.isModEnabled() && AntiLootrunTracker.config.isMobListEnabled() && disabledDuration <= 0;
    }
}