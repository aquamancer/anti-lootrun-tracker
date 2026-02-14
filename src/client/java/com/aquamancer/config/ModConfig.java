package com.aquamancer.config;

import com.aquamancer.AntiLootrunTrackerClient;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;

@Config(name = AntiLootrunTrackerClient.MOD_ID)
public class ModConfig implements ConfigData {
    private boolean modEnabled = true;
    private boolean mobListEnabled = true;
    private boolean renderNumber = true;
    private boolean recolorChest = true;

    public boolean isModEnabled() {
        return modEnabled;
    }

    public boolean isMobListEnabled() {
        return mobListEnabled;
    }

    public boolean renderNumber() {
        return renderNumber;
    }

    public boolean recolorChest() {
        return recolorChest;
    }
}
