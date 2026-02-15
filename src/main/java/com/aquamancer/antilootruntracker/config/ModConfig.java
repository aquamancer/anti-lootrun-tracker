package com.aquamancer.antilootruntracker.config;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import com.aquamancer.antilootruntracker.ColorManager;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.ColorPicker;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;

import me.shedaniel.math.Color;

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
    private boolean recolorChest = true;
    @Tooltip
    private ColorManager.ChestColor recolor = ColorManager.ChestColor.LIME;

    @Tooltip
    @CollapsibleObject
    MobListOptions mobListOptions = new MobListOptions();

    @Tooltip
    @CollapsibleObject
    ChestNumberOptions chestNumberOptions = new ChestNumberOptions();

    @Tooltip
    private List<String> ignoredMobs = new ArrayList<>() {{
        add("allay");
        add("villager");
    }};


    private static class MobListOptions {
        @Tooltip
        private boolean mobListEnabled = true;
        @Tooltip
        private int mobListReachDistance = 30;
        @Tooltip
        private int mobListDisableDuration = 20;
        @Tooltip
        private int mobListMaxChars = 80;
    }

    private static class ChestNumberOptions {
        @Tooltip
        private boolean renderNumber = true;
        @ColorPicker
        private int numberColor = 0xFF0000;
        @Tooltip
        @TransitiveObject
        private ChestFaces chestFaces = new ChestFaces();
    }

    private static class ChestFaces {
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
        return mobListOptions.mobListEnabled;
    }

    public int getMobListDisableDuration() {
        return mobListOptions.mobListDisableDuration;
    }

    public int getMobListMaxChars() {
        return mobListOptions.mobListMaxChars;
    }

    public int getMobListReachDistance() {
        return mobListOptions.mobListReachDistance;
    }

    public boolean renderNumber() {
        return chestNumberOptions.renderNumber;
    }

    public int getNumberColor() {
        return chestNumberOptions.numberColor;
    }

    public boolean renderTopFace() {
        return chestNumberOptions.chestFaces.topFace;
    }

    public boolean renderFrontFace() {
        return chestNumberOptions.chestFaces.frontFace;
    }

    public boolean renderBackFace() {
        return chestNumberOptions.chestFaces.backFace;
    }

    public boolean renderLeftFace() {
        return chestNumberOptions.chestFaces.leftFace;
    }

    public boolean renderRightFace() {
        return chestNumberOptions.chestFaces.rightFace;
    }

    public boolean renderBottomFace() {
        return chestNumberOptions.chestFaces.bottomFace;
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
