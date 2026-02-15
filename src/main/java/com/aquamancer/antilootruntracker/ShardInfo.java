package com.aquamancer.antilootruntracker;

import com.aquamancer.antilootruntracker.mixin.TabHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShardInfo {
    private static final Pattern shardRegex = Pattern.compile(".*<(?<shard>[-\\w\\d]*)>.*");

    public static String getCurrentShard() {
		Text headerText = ((TabHudAccessor) MinecraftClient.getInstance().inGameHud.getPlayerListHud()).getHeader();
        if (headerText == null) {
            return "";
        }

        String header = headerText.getString();
        Matcher matcher = shardRegex.matcher(header);
        if (matcher.matches()) {
            return matcher.group("shard");
        } else{
            return "";
        }
	}

//	public static String getShortShard() {
//		return getCurrentShard().replaceFirst("-\\d+$", "");
//	}
}
