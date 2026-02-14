package com.aquamancer;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.client.render.TexturedRenderLayers.CHEST_ATLAS_TEXTURE;

public class ColorManager {
    /**
     * Supported chest colors.
     */
    private enum Color {
        DEFAULT, WHITE, LIGHT_GRAY, GRAY, BLACK, BROWN, RED, ORANGE, YELLOW, LIME, GREEN, CYAN, LIGHT_BLUE, BLUE, PURPLE, MAGENTA, PINK
    }

    private static final Map<Map.Entry<Color, ChestType>, SpriteIdentifier> spriteLookup = new HashMap<>();

    static {
        for (Color color : Color.values()) {
            if (color == Color.DEFAULT) continue;

            String filename = color.name().toLowerCase();
            // add singlechest texture to map
            Map.Entry<Color, ChestType> key = new AbstractMap.SimpleImmutableEntry<>(color, ChestType.SINGLE);
            spriteLookup.put(key, createChestTextureId(filename));
            // add doublechest left texture to map
            key = new AbstractMap.SimpleImmutableEntry<>(color, ChestType.LEFT);
            spriteLookup.put(key, createChestTextureId(filename + "_left"));
            // add doublechest right texture to map
            key = new AbstractMap.SimpleImmutableEntry<>(color, ChestType.RIGHT);
            spriteLookup.put(key, createChestTextureId(filename + "_right"));
        }
    }

    private static SpriteIdentifier createChestTextureId(String variant) {
        return new SpriteIdentifier(CHEST_ATLAS_TEXTURE, new Identifier(AntiLootrunTrackerClient.MOD_ID, "entity/chest/" + variant));
    }

    /**
     * Returns a Chest texture based on how many mobs are near the chest.
     * @param chest the chest to check mobs in its surroundings
     * @param type the type of chest
     * @return a chest texture based on how many mobs are near the chest, null if the default Chest texture should be used
     */
    @Nullable
    public static SpriteIdentifier getChestSpriteBasedOnMobs(BlockEntity chest, ChestType type) {
        Color color = getChestColor(chest);
        return spriteLookup.get(new AbstractMap.SimpleImmutableEntry<>(color, type));
    }

    private static Color getChestColor(BlockEntity chest) {
        int mobsNearby = getMobsNearby(chest.getPos());
        if (mobsNearby <= 0) {
            return Color.LIME;
        } else {
            return Color.DEFAULT;
        }
    }

    public static int getMobsNearby(BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            return 0;
        }

        return client.world.getEntitiesByClass(
                MobEntity.class,
                new Box(pos).expand(AntiLootrunTrackerClient.MOB_SEARCH_RADIUS),
                LivingEntity::isAlive
        ).size();
    }

}
