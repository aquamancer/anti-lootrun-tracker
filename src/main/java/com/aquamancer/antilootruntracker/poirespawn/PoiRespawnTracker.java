package com.aquamancer.antilootruntracker.poirespawn;

import java.util.HashMap;
import java.util.Map;

public class PoiRespawnTracker {
    // key is shard
    private static final Map<String, Map<String, Long>> respawningPois = new HashMap<>();

    public static void addPoi(String name, int respawnInMinutes, String shard) {
        respawningPois.computeIfAbsent(shard, k -> new HashMap<>()).put(name, System.currentTimeMillis() + (long) respawnInMinutes * 60 * 1000);
    }
}
