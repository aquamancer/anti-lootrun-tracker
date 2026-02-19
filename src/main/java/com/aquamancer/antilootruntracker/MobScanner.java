package com.aquamancer.antilootruntracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MobScanner {
    public static final int MOB_SEARCH_RADIUS = 12;

    static final Map<BlockPos, List<MobEntity>> entityCache = new HashMap<>();
    private static int ticksUntilUpdate = 0;

    public static void onTick() {
        ticksUntilUpdate--;
        if (ticksUntilUpdate <= 0) {
            entityCache.clear();
            ticksUntilUpdate = AntiLootrunTracker.config.getEntityScanInterval();
        }
    }

    public static Stream<MobEntity> getMobsNearby(BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            return Stream.of();
        }

        return entityCache.computeIfAbsent(pos, (v) -> {
            return client.world.getEntitiesByClass(
                    MobEntity.class,
                    new Box(pos).expand(MOB_SEARCH_RADIUS),
                    mob -> {
                        return mob.isAlive()
                                && AntiLootrunTracker.config.getIgnoredBaseMobs().stream().noneMatch(ignored -> ignored.equalsIgnoreCase(EntityType.getId(mob.getType()).toString()))
                                && AntiLootrunTracker.config.getIgnoredMobNames().stream().noneMatch(ignored -> ignored.equalsIgnoreCase(
                                        (mob.getCustomName() != null ? mob.getCustomName() : (mob.getDisplayName() != null ? mob.getDisplayName() : Text.empty())).getString()));
                    }
            );
        }).stream().filter(LivingEntity::isAlive);
    }
}
