package com.aquamancer;

import com.aquamancer.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AntiLootrunTrackerClient implements ClientModInitializer {
	public static final String MOD_ID = "anti-lootrun-tracker";
//	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final int MOB_SEARCH_RADIUS = 12;
	private static long mobListDisableDurationTicks = 0;

	private static ModConfig config;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		config = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new).getConfig();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (mobListDisableDurationTicks > 0) {
				mobListDisableDurationTicks--;
			}
		});
	}

	// temporarily disables action bar enumeration and rendering of mob list
	public static void disableMobList(long ticks) {
		if (ticks > mobListDisableDurationTicks) {
			mobListDisableDurationTicks = ticks;
		}
	}

	public static boolean shouldRenderMobList() {
		return config.isModEnabled() && config.isMobListEnabled() && mobListDisableDurationTicks <= 0;
	}

	public static boolean shouldRenderMobCountOnChest() {
		return config.isModEnabled() && config.renderNumber();
	}

	public static boolean shouldRecolorFreeChest() {
		return config.isModEnabled() && config.recolorChest();
	}
}