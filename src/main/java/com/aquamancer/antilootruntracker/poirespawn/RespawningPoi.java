package com.aquamancer.antilootruntracker.poirespawn;

public class RespawningPoi {
    private String shard;
    private String name;
    private long respawnAtMillis;

    public RespawningPoi(String name, int minutesUntilRespawn, String shard) {
        this.name = name;
        this.respawnAtMillis = System.currentTimeMillis() + ((long) minutesUntilRespawn) * 60 * 1000;
        this.shard = shard;
    }

    public String getShard() {
        return shard;
    }

    public String getName() {
        return name;
    }

    public long getRespawnAtMillis() {
        return respawnAtMillis;
    }
}
