package com.aquamancer.antilootruntracker.config;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import com.aquamancer.antilootruntracker.ColorManager;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
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
    private int entityScanInterval = 4;
    @Tooltip
    private boolean mobListEnabled = true;
    @Tooltip
    private int mobListReachDistance = 30;
    @Tooltip
    private int mobListDisableDuration = 20;
    @Tooltip
    private int mobListMaxChars = 80;
    @Tooltip
    private boolean renderNumber = true;
    @Tooltip
    @CollapsibleObject
    private ChestFaces chestFaces = new ChestFaces();
    @Tooltip
    private boolean recolorChest = true;
    @Tooltip
    private ColorManager.ChestColor recolor = ColorManager.ChestColor.LIME;
    @Tooltip
    private List<String> ignoredMobs = new ArrayList<>() {{
        add("allay");
        add("villager");
    }};

    static class ChestFaces {
        private boolean topFace = true;
        private boolean frontFace = true;
        private boolean backFace = true;
        private boolean leftFace = false;
        private boolean rightFace = false;
        private boolean bottomFace = false;
    }

    public boolean isModEnabled() {
        return modEnabled;
    }

    public String getShardsEnabledIn() {
        return shardsEnabledIn;
    }

    public int getEntityScanInterval() {
        return entityScanInterval;
    }

    public boolean isMobListEnabled() {
        return mobListEnabled;
    }

    public int getMobListDisableDuration() {
        return mobListDisableDuration;
    }

    public int getMobListMaxChars() {
        return mobListMaxChars;
    }

    public int getMobListReachDistance() {
        return mobListReachDistance;
    }

    public boolean renderNumber() {
        return renderNumber;
    }

    public boolean renderTopFace() {
        return chestFaces.topFace;
    }

    public boolean renderFrontFace() {
        return chestFaces.frontFace;
    }

    public boolean renderBackFace() {
        return chestFaces.backFace;
    }

    public boolean renderLeftFace() {
        return chestFaces.leftFace;
    }

    public boolean renderRightFace() {
        return chestFaces.rightFace;
    }

    public boolean renderBottomFace() {
        return chestFaces.bottomFace;
    }

    public boolean recolorChest() {
        return recolorChest;
    }

    public ColorManager.ChestColor getRecolor() {
        return recolor;
    }

    public List<String> getIgnoredMobs() {
        return ignoredMobs;
    }
}
