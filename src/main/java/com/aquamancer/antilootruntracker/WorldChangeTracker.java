package com.aquamancer.antilootruntracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WorldChangeTracker {
    private static final List<Consumer<World>> listeners = new ArrayList<>();
    private static World lastWorld;

    public static void onTick(MinecraftClient client) {
        World currentWorld = client.world;
        if (lastWorld != currentWorld) {
            lastWorld = currentWorld;
            onWorldChange(currentWorld);
        }
    }

    public static void register(Consumer<World> callback) {
        listeners.add(callback);
    }

    private static void onWorldChange(World newWorld) {
        for (Consumer<World> callback : listeners) {
            callback.accept(newWorld);
        }
    }
}
