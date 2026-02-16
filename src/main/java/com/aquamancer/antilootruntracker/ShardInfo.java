package com.aquamancer.antilootruntracker;

import com.aquamancer.antilootruntracker.mixin.TabHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds the state of the current player's shard and handles update logic.
 */
public class ShardInfo {
    private static final Pattern shardRegex = Pattern.compile(".*<(?<shard>[-\\w\\d]*)>.*");
    private static final int updateIntervalTicks = 40;  // not configurable to user since updating shard is inexpensive

    private static String currentShard;
    private static boolean inValidShard;
    private static int ticksUntilUpdate;

    public static void onTick() {
        ticksUntilUpdate--;
        if (ticksUntilUpdate <= 0) {
            updateCurrentShard();
            inValidShard = Pattern.matches(AntiLootrunTracker.config.getShardsEnabledIn(), currentShard);
            ticksUntilUpdate = updateIntervalTicks;
        }
    }

    private static void updateCurrentShard() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.inGameHud == null || client.inGameHud.getPlayerListHud() == null) {
            return;
        }

		Text headerText = ((TabHudAccessor) client.inGameHud.getPlayerListHud()).getHeader();
        if (headerText == null) {
            currentShard = "";
            return;
        }

        String header = headerText.getString();
        Matcher matcher = shardRegex.matcher(header);
        if (matcher.matches()) {
            currentShard = matcher.group("shard");
        } else {
            currentShard = "";
        }
	}

    public static boolean inValidShard() {
        return inValidShard;
    }

//	public static String getShortShard() {
//		return getCurrentShard().replaceFirst("-\\d+$", "");
//	}
}
