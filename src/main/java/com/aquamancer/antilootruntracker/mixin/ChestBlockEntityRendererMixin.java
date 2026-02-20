package com.aquamancer.antilootruntracker.mixin;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import com.aquamancer.antilootruntracker.MobScanner;
import com.aquamancer.antilootruntracker.ShardInfo;
import com.aquamancer.antilootruntracker.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntityRenderer.class)
public class ChestBlockEntityRendererMixin {
    /**
     * Draws a number on faces of chests, indicating how many mobs are near it.
     */
    @Inject(at = @At("TAIL"), method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V")
    public void render(BlockEntity chest, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        // if !hasWorld() then the chest is being rendered in an inventory
        if (!isEnabled() || !(chest instanceof ChestBlockEntity) || !chest.hasWorld()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        World world = chest.getWorld();
        if (world == null || client == null) {
            return;
        }
        BlockPos chestPos = chest.getPos();
        long mobsNearby = Math.min(MobScanner.getMobsNearby(chestPos).count(), 9);  // # of mobs nearby, limit 1 digit
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
        float widthXOffset = -width / 2;
        int color = AntiLootrunTracker.config.getNumberColor();

        if (AntiLootrunTracker.config.renderTopFace())
            drawTopFace(text, color, widthXOffset, chestFacing, textRenderer,  matrices, vertexConsumers);
        if (AntiLootrunTracker.config.renderBottomFace())
            drawBottomFace(text, color, widthXOffset, chestFacing, textRenderer,  matrices, vertexConsumers);
        if (AntiLootrunTracker.config.renderFrontFace())
            drawFrontFace(text, color, widthXOffset, chestFacing, textRenderer,  matrices, vertexConsumers);
        if (AntiLootrunTracker.config.renderBackFace())
            drawBackFace(text, color, widthXOffset, chestFacing, textRenderer,  matrices, vertexConsumers);
        if (AntiLootrunTracker.config.renderRightFace())
            drawRightFace(text, color, widthXOffset, chestFacing, textRenderer,  matrices, vertexConsumers);
        if (AntiLootrunTracker.config.renderLeftFace())
            drawLeftFace(text, color, widthXOffset, chestFacing, textRenderer,  matrices, vertexConsumers);
    }

    // RotationAxis.POSITIVE_X is the axis from left face to right face, rotationDegrees rotates clockwise when viewing left face
    // POSITIVE_Z is axis from front to back face, rotationDegrees rotates clockwise when viewing front
    // POSITIVE_Y is axis from top to bottom, rotationdegrees rotatesclockwise when viewing top
    @Unique
    private void drawFrontFace(String text, int color, float widthXOffset, Direction chestFacing, TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);  // center of chest
        // translate 0.5 (+0.05 to avoid clipping with the chest's lock) units towards the front of chest
        Vector3f offset = chestFacing.getUnitVector().mul(0.525f);
        matrices.translate(offset.x, offset.y, offset.z);
        // face the correct orientation
        matrices.multiply(chestFacing.getRotationQuaternion());  // correct orientation, but facing up
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));  // facing front
        matrices.scale(1/18f, 1/18f, 1/18f);
        textRenderer.draw(
                text,
                widthXOffset,
                -4f,
                color,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                0xF000F0  // max lighting
        );
        matrices.pop();
    }

    @Unique
    private void drawTopFace(String text, int color, float widthXOffset, Direction chestFacing, TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();
        matrices.translate(0.5, 0.925, 0.5);  // top of chest, centered
        matrices.scale(1/18f, 1/18f, 1/18f);
        // face the correct orientation
        matrices.multiply(chestFacing.getRotationQuaternion());
        textRenderer.draw(
                text,
                widthXOffset,
                -4f,
                color,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                0xF000F0  // max lighting
        );
        matrices.pop();
    }

    @Unique
    private void drawBottomFace(String text, int color, float widthXOffset, Direction chestFacing, TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();
        matrices.translate(0.5, -0.025, 0.5);  // bottom of chest, centered
        matrices.scale(1/18f, 1/18f, 1/18f);
        // face the correct orientation
        matrices.multiply(chestFacing.getRotationQuaternion());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));  // flip to face downward
        textRenderer.draw(
                text,
                widthXOffset,
                -4f,
                color,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                0xF000F0  // max lighting
        );
        matrices.pop();
    }

    @Unique
    private void drawRightFace(String text, int color, float widthXOffset, Direction chestFacing, TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5); // center of chest
        Vector3f rightOffset = chestFacing.getUnitVector().rotateY(90).mul(0.525f); // move right
        matrices.translate(rightOffset.x, rightOffset.y, rightOffset.z);
        Vector3f frontOffset = chestFacing.getUnitVector().mul(0.2f); // move front a bit to account for text width
        matrices.translate(frontOffset.x, frontOffset.y, frontOffset.z);
        matrices.multiply(chestFacing.getRotationQuaternion());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90)); // upright
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270)); // facing right
        matrices.scale(1/18f, 1/18f, 1/18f);
        textRenderer.draw(
                text,
                widthXOffset,
                -4f,
                color,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                0xF000F0
        );
        matrices.pop();
    }

    @Unique
    private void drawLeftFace(String text, int color, float widthXOffset, Direction chestFacing, TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5); // center of chest
        Vector3f leftOffset = chestFacing.getUnitVector().rotateY(-90).mul(0.525f); // move left
        matrices.translate(leftOffset.x, leftOffset.y, leftOffset.z);
        Vector3f frontOffset = chestFacing.getUnitVector().mul(0.3f); // move front a bit to account for text width
        matrices.translate(frontOffset.x, frontOffset.y, frontOffset.z);
        matrices.multiply(chestFacing.getRotationQuaternion());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90)); // upright
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90)); // facing right
        matrices.scale(1/18f, 1/18f, 1/18f);
        textRenderer.draw(
                text,
                widthXOffset,
                -4f,
                color,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                0xF000F0
        );
        matrices.pop();
    }

    @Unique
    private void drawBackFace(String text, int color, float widthXOffset, Direction chestFacing, TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);  // center of chest
        Vector3f offset = chestFacing.getUnitVector().mul(-0.525f);
        matrices.translate(offset.x, offset.y, offset.z);
        // face the correct orientation
        matrices.multiply(chestFacing.getRotationQuaternion());  // correct orientation, but facing up
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));  // facing upright
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));  // facing back
        matrices.scale(1/18f, 1/18f, 1/18f);
        textRenderer.draw(
                text,
                widthXOffset,
                -4f,
                color,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                0xF000F0  // max lighting
        );
        matrices.pop();
    }

    @Unique
    private static boolean isEnabled() {
        ModConfig config = AntiLootrunTracker.config;
        return config.isModEnabled() && ShardInfo.inValidShard() && config.renderNumber();
    }
}
