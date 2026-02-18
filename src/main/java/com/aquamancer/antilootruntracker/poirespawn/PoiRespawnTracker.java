package com.aquamancer.antilootruntracker.poirespawn;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PoiRespawnTracker {
    private static final int CLEANUP_INTERVAL_TICKS = 20;
    private static int ticksUntilCleanup = 0;
    // Map<shard, <poi name, system respawn time>>
    private static final Map<String, Map<String, Long>> respawningPois = new HashMap<>();

    public static void addPoi(String name, int respawnInMinutes, String shard) {
        if (respawningPois.values().stream().mapToInt(Map::size).sum() > 100) {
            // prevent abuse
            return;
        }
        respawningPois.computeIfAbsent(shard, k -> new HashMap<>()).put(name, System.currentTimeMillis() + (long) respawnInMinutes * 60 * 1000);
    }

    public static void onTick() {
        ticksUntilCleanup--;
        if (ticksUntilCleanup <= 0) {
            removeRespawnedPois();
            ticksUntilCleanup = CLEANUP_INTERVAL_TICKS;
        }
    }

    public static void renderTimersInTooltip(ItemStack stack, List<Text> lines) {
        if (!AntiLootrunTracker.config.showRespawningPoisInShardSelector()) {
            return;
        }
        if (stack == null || lines == null || lines.isEmpty()) {
            return;
        }
        String itemName = lines.get(0).getString();
        Map<String, Long> pois = respawningPois.get(itemName);
        if (pois != null && !pois.isEmpty()) {
            lines.add(Text.empty());  // add blank line to separate
            pois.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach((poi) -> {
                        String timeUntilRespawn = getTimeUntil(poi.getValue());
                        MutableText timer = Text.literal(poi.getKey()).append(": ").formatted(Formatting.GOLD);
                        timer.append(Text.literal(timeUntilRespawn).formatted(Formatting.BLUE));
                        lines.add(timer);
                    });
        }
    }

    // this should be safe assuming ClientTickEvents and ItemTooltipCallback run on the same thread
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
        if (!AntiLootrunTracker.config.displayPoiRespawnMessage()) {
            return;
        }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        MutableText message = Text.empty();
        message.append(Text.literal(name).formatted(Formatting.GOLD, Formatting.BOLD));
        message.append(Text.literal(" has respawned in "));
        message.append(Text.literal(shard).formatted(Formatting.AQUA, Formatting.ITALIC));
        player.sendMessage(message);
    }

    private static String getTimeUntil(long systemMillis) {
        long untilMillis = systemMillis - System.currentTimeMillis();
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
