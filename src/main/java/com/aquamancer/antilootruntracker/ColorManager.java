package com.aquamancer.antilootruntracker;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.client.render.TexturedRenderLayers.CHEST_ATLAS_TEXTURE;

public class ColorManager {
    /**
     * Supported chest colors.
     */
    // made public so ModConfig can access it
    public enum ChestColor {
        DEFAULT, WHITE, LIGHT_GRAY, GRAY, BLACK, BROWN, RED, ORANGE, YELLOW, LIME, GREEN, CYAN, LIGHT_BLUE, BLUE, PURPLE, MAGENTA, PINK
    }

    private static final Map<Map.Entry<ChestColor, ChestType>, SpriteIdentifier> spriteLookup = new HashMap<>();

    static {
        for (ChestColor color : ChestColor.values()) {
            if (color == ChestColor.DEFAULT) continue;

            String filename = color.name().toLowerCase();
            // add singlechest texture to map
            Map.Entry<ChestColor, ChestType> key = new AbstractMap.SimpleImmutableEntry<>(color, ChestType.SINGLE);
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
        return new SpriteIdentifier(CHEST_ATLAS_TEXTURE, new Identifier(AntiLootrunTracker.MOD_ID, "entity/chest/" + variant));
    }

    /**
     * Returns a Chest texture based on how many mobs are near the chest.
     * @param chest the chest to check mobs in its surroundings
     * @param type the type of chest
     * @return a chest texture based on how many mobs are near the chest, null if the default Chest texture should be used
     */
    @Nullable
    public static SpriteIdentifier getChestSpriteBasedOnMobs(BlockEntity chest, ChestType type) {
        ChestColor color = getChestColor(chest);
        return spriteLookup.get(new AbstractMap.SimpleImmutableEntry<>(color, type));
    }

    private static ChestColor getChestColor(BlockEntity chest) {
        long mobsNearby = MobScanner.getMobsNearby(chest.getPos()).count();
        if (mobsNearby == 0) {
            return AntiLootrunTracker.config.getFreeChestRecolor();
        } else {
            return ChestColor.DEFAULT;
        }
    }

    public static SpriteIdentifier getChestSprite(ChestColor color, ChestType type) {
        return spriteLookup.get(new AbstractMap.SimpleImmutableEntry<>(color, type));
    }
}
