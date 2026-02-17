package com.aquamancer.antilootruntracker.poirespawn;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PoiRespawnTracker {
    private static final int CLEANUP_INTERVAL_TICKS = 20;
    private static int ticksUntilCleanup = 0;
    // key is shard
    private static final Map<String, Map<String, Long>> respawningPois = new HashMap<>();

    public static void addPoi(String name, int respawnInMinutes, String shard) {
        respawningPois.computeIfAbsent(shard, k -> new HashMap<>()).put(name, System.currentTimeMillis() + (long) respawnInMinutes * 60 * 1000);
    }

    public static void onTick() {
        ticksUntilCleanup--;
        if (ticksUntilCleanup <= 0) {
            removeRespawnedPois();
            ticksUntilCleanup = CLEANUP_INTERVAL_TICKS;
        }
    }

    public static void renderTimersInTooltip(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (stack == null || lines == null) {
            return;
        }
        String itemName = stack.getItem().getName().getString();
        if (respawningPois.get(itemName) != null) {
            for (Map.Entry<String, Long> poi : respawningPois.get(itemName).entrySet()) {
                String timeUntilRespawn = getTimeUntil(poi.getValue());
                if (timeUntilRespawn == null) {
                    continue;
                }
                MutableText timer = Text.literal(poi.getKey()).append(" respawning in: ").append(timeUntilRespawn);
                lines.add(timer);
            }
        }
    }

    private static void removeRespawnedPois() {
        Iterator<Map.Entry<String, Map<String, Long>>> shardIterator = respawningPois.entrySet().iterator();
        while (shardIterator.hasNext()) {
            Map.Entry<String, Map<String, Long>> shard = shardIterator.next();
            Iterator<Map.Entry<String, Long>> poiIterator = shard.getValue().entrySet().iterator();
            while (poiIterator.hasNext()) {
                Map.Entry<String, Long> poi = poiIterator.next();
                if (System.currentTimeMillis() >= poi.getValue()) {
                    onPoiRespawned(poi.getKey(), shard.getKey());
                    poiIterator.remove();
                }
            }
            if (shard.getValue().isEmpty()) {
                shardIterator.remove();
            }
        }
    }

    private static void onPoiRespawned(String name, String shard) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        MutableText message = Text.literal(name).formatted(Formatting.GOLD, Formatting.BOLD);
        message.append(Text.literal(" has respawned in ").formatted(Formatting.GOLD));
        message.append(Text.literal(shard).formatted(Formatting.AQUA, Formatting.ITALIC));
        player.sendMessage(message);
    }

    @Nullable
    private static String getTimeUntil(long systemMillis) {
        long untilMillis = systemMillis - System.currentTimeMillis();
        if (untilMillis <= 0) {
            return null;
        }

        long minutes = untilMillis / 60000;
        long seconds = (untilMillis % 60000) / 1000;
        String result = "";
        if (minutes > 0) {
            result += String.valueOf(minutes);
            result += "m";
        }
        result += String.valueOf(seconds);
        result += "s";
        return result;
    }
}
