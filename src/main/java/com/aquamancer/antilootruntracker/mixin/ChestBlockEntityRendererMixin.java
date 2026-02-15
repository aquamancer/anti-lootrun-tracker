package com.aquamancer.antilootruntracker.mixin;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ChestBlockEntityRenderer.class)
public class ChestBlockEntityRendererMixin {
    /**
     * Draws a number on the top and front face of chests, indicating how many mobs are near it.
     */
    @Inject(at = @At("TAIL"), method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V")
    public void render(BlockEntity chest, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        if (!AntiLootrunTracker.shouldRenderMobCountOnChest()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        World world = chest.getWorld();
        if (world == null || client == null) {
            return;
        }
        BlockPos chestPos = chest.getPos();
        int mobsNearby = Math.min(AntiLootrunTracker.getMobsNearby(chestPos).size(), 9);  // # of mobs nearby, limit 1 digit
        if (mobsNearby == 0) {
            return;
        }
        BlockState chestState = world.getBlockState(chestPos);
        Direction chestFacing;
        try {
            chestFacing = chestState.get(ChestBlock.FACING);
        } catch (IllegalArgumentException | NullPointerException ex) {
            // IllegalArgument thrown when chest is broken since the block becomes minecraft:air
            return;
        }
        TextRenderer textRenderer = client.textRenderer;
        String text = String.valueOf(mobsNearby);
        float width = textRenderer.getWidth(text);

        // draw # of mobs on Chest
        // TOP FACE
        matrices.push();
        matrices.translate(0.5, 1.0, 0.5);  // top of chest, centered
        matrices.scale(1/18f, 1/18f, 1/18f);
        // face the correct orientation
        matrices.multiply(chestFacing.getRotationQuaternion());
        textRenderer.draw(
                text,
                -width/2f,
                -4f,
                Color.RED.getRGB(),
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                0xF000F0  // max lighting
        );
        matrices.pop();

        // FRONT FACE
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);  // center of chest
        // translate 0.5 (+0.05 to avoid clipping with the chest's lock) units towards the front of chest
        Vector3f offset = chestFacing.getUnitVector().mul(0.55f);
        matrices.translate(offset.x, offset.y, offset.z);
        // face the correct orientation
        matrices.multiply(chestFacing.getRotationQuaternion());  // correct orientation, but facing up
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));  // facing front
        matrices.scale(1/18f, 1/18f, 1/18f);
        textRenderer.draw(
                text,
                -width / 2f,
                -4f,
                Color.RED.getRGB(),
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                0xF000F0  // max lighting
        );
        matrices.pop();
    }
}
