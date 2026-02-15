package com.aquamancer.antilootruntracker.config;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

import java.util.ArrayList;
import java.util.List;

@Config(name = AntiLootrunTracker.MOD_ID)
public class ModConfig implements ConfigData {
    @Tooltip
    private boolean modEnabled = true;
    @Tooltip
    private String shardsEnabledIn = "(valley|isles|ring|skt).*";
    @Tooltip
    private boolean mobListEnabled = true;
    @Tooltip
    private int mobListReachDistance = 30;
    @Tooltip
    private int mobListDisableDuration = 10;
    @Tooltip
    private boolean renderNumber = true;
    @Tooltip
    private boolean recolorChest = true;
    @Tooltip
    private List<String> ignoredMobs = new ArrayList<>() {{
        add("allay");
        add("villager");
    }};

    public boolean isModEnabled() {
        return modEnabled;
    }

    public String getShardsEnabledIn() {
        return shardsEnabledIn;
    }

    public boolean isMobListEnabled() {
        return mobListEnabled;
    }

    public int getMobListDisableDuration() {
        return mobListDisableDuration;
    }

    public int getMobListReachDistance() {
        return mobListReachDistance;
    }

    public boolean renderNumber() {
        return renderNumber;
    }

    public boolean recolorChest() {
        return recolorChest;
    }

    public List<String> getIgnoredMobs() {
        return ignoredMobs;
    }
}
