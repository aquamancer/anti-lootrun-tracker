package com.aquamancer.antilootruntracker.poirespawn;

import com.aquamancer.antilootruntracker.ShardInfo;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoiRespawnTracker {
    // key is shard
    private static final Map<String, Map<String, Long>> respawningPois = new HashMap<>();

    public static void addPoi(String name, int respawnInMinutes, String shard) {
        respawningPois.computeIfAbsent(shard, k -> new HashMap<>()).put(name, System.currentTimeMillis() + (long) respawnInMinutes * 60 * 1000);
    }
}
