package com.aquamancer.antilootruntracker.poirespawn;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PoiRespawnTracker {
    // key is shard
    private static final Map<String, Map<String, Long>> respawningPois = new HashMap<>();

    public static void addPoi(String name, int respawnInMinutes, String shard) {
        respawningPois.computeIfAbsent(shard, k -> new HashMap<>()).put(name, System.currentTimeMillis() + (long) respawnInMinutes * 60 * 1000);
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
                MutableText timer = Text.literal(poi.getKey()).append("respawning in: ").append(timeUntilRespawn);
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
                if (poi.getValue() <= 0) {

                }
            }
        }
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
