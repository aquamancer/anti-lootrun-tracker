package com.aquamancer.antilootruntracker.config;

import com.aquamancer.antilootruntracker.AntiLootrunTracker;
import com.aquamancer.antilootruntracker.ColorManager;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.ColorPicker;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;

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
    @CollapsibleObject
    MobListOptions mobListOptions = new MobListOptions();

    @Tooltip
    @CollapsibleObject
    ChestNumberOptions chestNumberOptions = new ChestNumberOptions();

    @CollapsibleObject
    ChestRecolorOptions chestRecolorOptions = new ChestRecolorOptions();

    @CollapsibleObject
    PoiRespawnOptions poiRespawnOptions = new PoiRespawnOptions();

    @Tooltip
    private List<String> ignoredBaseMobs = new ArrayList<>() {{
        add("minecraft:villager");
    }};

    @Tooltip
    private List<String> ignoredMobNames = new ArrayList<>() {{
        // alchemist
        add("AlchemicalGrenade");  // alchemical artillery
        add("Unstable Amalgam");  // unstable amalgam
        // alchemist/harbinger
        add("Alchemical Aberration");  // esoteric enhancements

        // scout hunting companion
        add("Fox Companion");  // on land
        add("Axolotl Companion");  // in water
        add("Strider Companion");  // in lava
        add("Eagle Companion");  // skill enhancement: not in water
        add("Dolphin Companion");  // skill enhancement: in water
        // skin
        add("Twisted Companion");  // replaces Fox Companion

        // warlock/tenebrist
        add("Restless Soul");
        // skin is called fae spirits but the entity is still named Restless Soul(?)

        // cleric/seraph
        add("Keeper Virtue");
        // skin
        add("Vigilant Mothdragon");
    }};

    private static class MobListOptions {
        @Tooltip
        private boolean mobListEnabled = true;
        @Tooltip
        private int mobListUpdateInterval = 1;
        @Tooltip
        private int mobListReachDistance = 30;
        @Tooltip
        private int mobListDisableDuration = 20;
        @Tooltip
        private int mobListMaxChars = 80;
        @Tooltip
        @TransitiveObject
        private FreeMessageOptions freeMessageOptions = new FreeMessageOptions();

        private static class FreeMessageOptions {
            @Tooltip
            private boolean displayFreeChestMessage = false;
            @Tooltip
            private String freeChestMessage = "FREE CHEST";
        }
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

    private static class ChestRecolorOptions {
        @Tooltip
        private boolean recolorFreeChests = true;
        @Tooltip
        private ColorManager.ChestColor freeChestRecolor = ColorManager.ChestColor.LIME;
        @Tooltip
        private boolean recolorAllChests = false;
        @Tooltip
        private ColorManager.ChestColor allChestRecolor = ColorManager.ChestColor.CYAN;
        @Tooltip
        private boolean shouldIgnoreShardFilter = true;
    }

    private static class PoiRespawnOptions {
        private boolean appendShard = true;
        @Tooltip
        private boolean sendPoiRespawnMessage = true;
        @Tooltip
        private boolean showRespawningInShardSelector = true;
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

    public boolean recolorAllChests() {
        return chestRecolorOptions.recolorAllChests;
    }

    public ColorManager.ChestColor getAllChestRecolor() {
        return chestRecolorOptions.allChestRecolor;
    }

    public boolean recolorFreeChests() {
        return chestRecolorOptions.recolorFreeChests;
    }

    public ColorManager.ChestColor getFreeChestRecolor() {
        return chestRecolorOptions.freeChestRecolor;
    }

    public boolean shouldAllChestRecolorIgnoreShard() {
        return chestRecolorOptions.shouldIgnoreShardFilter;
    }

    public boolean isMobListEnabled() {
        return mobListOptions.mobListEnabled;
    }

    public int getMobListUpdateInterval() {
        return mobListOptions.mobListUpdateInterval;
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

    public boolean displayFreeChestMessage() {
        return mobListOptions.freeMessageOptions.displayFreeChestMessage;
    }

    public String getFreeChestMessage() {
        return mobListOptions.freeMessageOptions.freeChestMessage;
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

    public boolean appendShardToConquerMessage() {
        return poiRespawnOptions.appendShard;
    }

    public boolean displayPoiRespawnMessage() {
        return poiRespawnOptions.sendPoiRespawnMessage;
    }

    public boolean showRespawningPoisInShardSelector() {
        return poiRespawnOptions.showRespawningInShardSelector;
    }

    public List<String> getIgnoredBaseMobs() {
        return ignoredBaseMobs;
    }

    public List<String> getIgnoredMobNames() {
        return ignoredMobNames;
    }
}
